package com.COMP3040.NanjingGo.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.VideoView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.COMP3040.NanjingGo.Adapter.BannerAdapter
import com.COMP3040.NanjingGo.Adapter.TopLocationAdapter
import com.COMP3040.NanjingGo.R
import com.COMP3040.NanjingGo.ViewModel.MainViewModel
import com.COMP3040.NanjingGo.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Calendar

/**
 * MainActivity is the entry point of the application after login.
 * It handles user-specific features, such as displaying a welcome message,
 * managing the banner, and listing top locations based on the season.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels() // Use lifecycle-aware ViewModel
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")

        // 1. 通过 ViewBinding 绑定布局
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. 初始化 Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        // 3. 设置欢迎语
        setWelcomeMessage()

        // 4. 加载用户头像
        loadUserProfileImage()

        // 5. 初始化轮播 Banner
        initBanner()

        // 6. 初始化季节性热门景点
        initTopLocation()

        // 7. 设置底部按钮点击事件（这里会包含 llExplorer）
        setupClickListeners()

        // 8. 设置小铃铛动画
        setupBellVideoView()
    }

    /**
     * Set a personalized welcome message with the user's email prefix or "Guest" if not logged in.
     */
    private fun setWelcomeMessage() {
        val currentUser = firebaseAuth.currentUser
        val userName = currentUser?.email?.substringBefore("@") ?: "Guest"
        binding.textView2.text = "Hi, $userName"
    }

    /**
     * Load the user's profile image from Firebase database.
     * If no image URL is found, set a default profile picture.
     */
    private fun loadUserProfileImage() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val profileImageRef = databaseReference
                .child("users")
                .child(userId)
                .child("profileImageUrl")

            profileImageRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profileImageUrl = snapshot.getValue(String::class.java)
                    if (!profileImageUrl.isNullOrEmpty()) {
                        // Load the profile image using Glide
                        Glide.with(this@MainActivity)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.default_profile_picture)
                            .into(binding.profileImageView)
                    } else {
                        binding.profileImageView.setImageResource(R.drawable.default_profile_picture)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MainActivity", "Failed to load profile image: ${error.message}")
                    binding.profileImageView.setImageResource(R.drawable.default_profile_picture)
                }
            })
        } else {
            binding.profileImageView.setImageResource(R.drawable.default_profile_picture)
        }
    }

    /**
     * Set up the banner ViewPager2 for displaying a carousel of images.
     * Includes auto-slide functionality and page indicator dots.
     */
    private fun initBanner() {
        val bannerImages = listOf(
            R.drawable.banner1,
            R.drawable.banner2,
            R.drawable.banner3
        )

        val bannerAdapter = BannerAdapter(bannerImages)
        binding.bannerViewPager.adapter = bannerAdapter

        // Add dots for indicators and handle page changes
        addIndicatorDots(bannerImages.size)
        binding.bannerViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicatorDots(position)
            }
        })

        // Auto-slide functionality
        val handler = android.os.Handler()
        val runnable = object : Runnable {
            var currentPage = bannerImages.size - 1 // Start from last
            var isReverse = true

            override fun run() {
                if (currentPage < 0) {
                    currentPage = bannerImages.size - 1
                }
                binding.bannerViewPager.setCurrentItem(currentPage, true)
                if (isReverse) currentPage-- else currentPage++
                handler.postDelayed(this, 3000)
            }
        }

        handler.postDelayed(runnable, 3000)
        binding.bannerViewPager.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                handler.postDelayed(runnable, 3000)
            }

            override fun onViewDetachedFromWindow(v: View) {
                handler.removeCallbacksAndMessages(null)
            }
        })
    }

    /**
     * Add indicator dots for the banner.
     * @param count Number of dots to add based on the number of banners.
     */
    private fun addIndicatorDots(count: Int) {
        binding.bannerIndicator.removeAllViews()
        for (i in 0 until count) {
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(16, 16).apply {
                    setMargins(8, 0, 8, 0)
                }
                background = ContextCompat.getDrawable(this@MainActivity, R.drawable.dot_inactive)
            }
            binding.bannerIndicator.addView(dot)
        }
    }

    /**
     * Update the appearance of indicator dots based on the currently selected banner position.
     * @param selectedPosition The index of the currently selected banner.
     */
    private fun updateIndicatorDots(selectedPosition: Int) {
        for (i in 0 until binding.bannerIndicator.childCount) {
            val dot = binding.bannerIndicator.getChildAt(i)
            dot.background = if (i == selectedPosition) {
                ContextCompat.getDrawable(this@MainActivity, R.drawable.dot_active)
            } else {
                ContextCompat.getDrawable(this@MainActivity, R.drawable.dot_inactive)
            }
        }
    }

    /**
     * Determine the current season based on the current month.
     * @return A string representing the season: SPRING, SUMMER, AUTUMN, or WINTER.
     */
    fun getCurrentSeason(): String {
        val month = Calendar.getInstance().get(Calendar.MONTH) // 0-based month index
        return when (month) {
            in 2..4 -> "SPRING"
            in 5..7 -> "SUMMER"
            in 8..10 -> "AUTUMN"
            else -> "WINTER"
        }
    }

    /**
     * Initialize the RecyclerView for top locations based on the current season.
     */
    private fun initTopLocation() {
        binding.apply {
            progressBarTopLocation.visibility = View.VISIBLE

            val currentSeason = getCurrentSeason()
            viewModel.locations.observe(this@MainActivity) { locations ->
                if (locations.isNullOrEmpty()) {
                    recyclerViewTopLocation.visibility = View.GONE
                } else {
                    recyclerViewTopLocation.visibility = View.VISIBLE
                    recyclerViewTopLocation.layoutManager =
                        LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                    recyclerViewTopLocation.adapter = TopLocationAdapter(locations)
                }
                progressBarTopLocation.visibility = View.GONE
            }

            viewModel.loadLocations(currentSeason)
            locationListTxt.setOnClickListener {
                startActivity(Intent(this@MainActivity, TopLocationsActivity::class.java))
            }
        }
    }

    /**
     * Set up the VideoView for the bell animation and ensure it loops continuously.
     */
    private fun setupBellVideoView() {
        val bellVideoView: VideoView = binding.bellVideoView
        val videoPath = "android.resource://${packageName}/${R.raw.bell_animation}"
        bellVideoView.setVideoURI(Uri.parse(videoPath))

        bellVideoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            mediaPlayer.setVolume(0f, 0f)
        }
        bellVideoView.start()
        bellVideoView.setOnCompletionListener { bellVideoView.start() }
    }

    override fun onPause() {
        super.onPause()
        binding.bellVideoView.pause()
    }

    override fun onResume() {
        super.onResume()
        binding.bellVideoView.start()
    }

    /**
     * Set up click listeners for various buttons on the main screen.
     */
    private fun setupClickListeners() {
        // 这里是你已经有的点击事件
        binding.favoriteLocationsButton.setOnClickListener {
            startActivity(Intent(this, FavoriteLocationListActivity::class.java))
        }

        binding.accountButton.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }

        // 给 llExplorer 添加点击事件 ----
        binding.llExplorer.setOnClickListener {

            startActivity(Intent(this, AiChatActivity::class.java))


        }
    }

    /**
     * Clear saved user credentials, such as email and password, from SharedPreferences.
     */
    private fun clearSavedCredentials() {
        val sharedPref = getSharedPreferences("user_credentials", MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("saved_email")
            remove("saved_password")
            apply()
        }
    }
}
