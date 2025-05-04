package com.COMP3040.NanjingGo.Activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.COMP3040.NanjingGo.R
import com.COMP3040.NanjingGo.databinding.ActivityAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException

/**
 * Activity for managing user accounts.
 * This activity provides functionalities to:
 * - Display user details (username and email).
 * - Update profile pictures.
 * - Log out from the application.
 */
class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding // Binding object for layout views
    private lateinit var firebaseAuth: FirebaseAuth // Firebase Authentication instance
    private lateinit var firebaseStorage: FirebaseStorage // Firebase Storage instance
    private var imageUri: Uri? = null // URI for selected profile image
    private var isUploading = false

    // 注册图片选择结果处理器
    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                imageUri = uri
                showImagePreviewDialog(uri)
            }
        }
    }

    // 注册相机拍照结果处理器
    private val takePhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                imageUri = uri
                showImagePreviewDialog(uri)
            }
        }
    }

    /**
     * Called when the activity is created. Initializes UI elements and sets up Firebase instances.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     * being shut down, this contains the data it most recently supplied in `onSaveInstanceState`.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth and Storage
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        setupUI()
        setupProfileImage()
        fetchProfileImage()

        // Handle back button functionality
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        // Allow user to select a profile image
        binding.profileImageContainer.setOnClickListener {
            if (!isUploading) {
                showImagePickerDialog()
            } else {
                Toast.makeText(this, "Please wait while the current upload completes", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle logout button click
        binding.logoutButton.setOnClickListener {
            firebaseAuth.signOut() // Sign out from Firebase
            clearSavedCredentials() // Clear saved credentials
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Close the activity
        }
    }

    private fun setupUI() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let {
            val email = it.email ?: "No Email"
            val username = email.substringBefore("@") // Extract username from the email address

            // Display username and email in the UI
            binding.usernameText.text = username
            binding.emailText.text = email
        }

        // 添加编辑头像的提示文本和图标
        binding.editProfileText.visibility = View.VISIBLE
        binding.editProfileIcon.visibility = View.VISIBLE
    }

    private fun setupProfileImage() {
        // 添加头像加载动画
        binding.profileImage.alpha = 0f
        binding.profileImage.animate().alpha(1f).setDuration(300).start()
        
        // 设置默认头像
        binding.profileImage.setImageResource(R.drawable.default_profile_picture)
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        AlertDialog.Builder(this)
            .setTitle("Update Profile Picture")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> launchCamera()
                    1 -> launchGallery()
                    2 -> dialog.dismiss()
                }
            }
            .show()
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePhoto.launch(intent)
    }

    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(intent)
    }

    private fun showImagePreviewDialog(uri: Uri) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Preview Profile Picture")
            .setView(R.layout.dialog_image_preview)
            .setPositiveButton("Upload") { _, _ -> uploadImage(uri) }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        // 在预览对话框中显示图片
        dialog.findViewById<ImageView>(R.id.previewImage)?.let { imageView ->
            Glide.with(this)
                .load(uri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
        }
    }

    private fun uploadImage(uri: Uri) {
        if (isUploading) return

        isUploading = true
        showUploadProgress(true)

        val userId = firebaseAuth.currentUser?.uid ?: return
        val imageRef = firebaseStorage.reference.child("profile_images/$userId.jpg")

        imageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                    updateProfileImageUrl(downloadUrl.toString())
                }
            }
            .addOnFailureListener { e ->
                showUploadProgress(false)
                isUploading = false
                Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            .addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                updateUploadProgress(progress.toInt())
            }
    }

    private fun updateProfileImageUrl(url: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        FirebaseDatabase.getInstance().getReference("users/$userId/profileImageUrl")
            .setValue(url)
            .addOnSuccessListener {
                showUploadProgress(false)
                isUploading = false
                Toast.makeText(this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show()
                loadProfileImage(url)
            }
            .addOnFailureListener { e ->
                showUploadProgress(false)
                isUploading = false
                Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchProfileImage() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        FirebaseDatabase.getInstance().getReference("users/$userId/profileImageUrl")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profileImageUrl = snapshot.getValue(String::class.java)
                    if (!profileImageUrl.isNullOrEmpty()) {
                        loadProfileImage(profileImageUrl)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AccountActivity, "Failed to fetch profile image", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadProfileImage(url: String) {
        Glide.with(this)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.default_profile_picture)
            .error(R.drawable.default_profile_picture)
            .into(binding.profileImage)
    }

    private fun showUploadProgress(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.profileImage.alpha = if (show) 0.5f else 1.0f
    }

    private fun updateUploadProgress(progress: Int) {
        binding.progressBar.progress = progress
    }

    /**
     * Clears saved login credentials from SharedPreferences.
     */
    private fun clearSavedCredentials() {
        val sharedPref = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("email")
            remove("password")
            putBoolean("rememberMe", false)
            apply()
        }
    }
}
