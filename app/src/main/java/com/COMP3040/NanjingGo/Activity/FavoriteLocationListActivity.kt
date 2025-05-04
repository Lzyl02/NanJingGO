package com.COMP3040.NanjingGo.Activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.COMP3040.NanjingGo.Adapter.FavoriteLocationAdapter
import com.COMP3040.NanjingGo.ViewModel.MainViewModel
import com.COMP3040.NanjingGo.databinding.ActivityFavoriteLocationListBinding
import com.google.firebase.auth.FirebaseAuth

/**
 * Activity to display a list of favorite locations for the current user.
 *
 * This activity fetches the favorite locations of the currently authenticated user
 * from Firebase and displays them in a RecyclerView.
 */
class FavoriteLocationListActivity : BaseActivity() {

    private lateinit var binding: ActivityFavoriteLocationListBinding // View binding for the layout
    private val viewModel = MainViewModel() // ViewModel for managing and observing favorite locations

    companion object {
        const val TAG = "FavoriteLocationListActivity" // Tag for logging purposes
    }

    /**
     * Called when the activity is created.
     * Sets up the layout, fetches the user's favorite locations, and initializes the RecyclerView.
     *
     * @param savedInstanceState If the activity is being re-created from a previous state,
     * this is the data it most recently supplied. Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteLocationListBinding.inflate(layoutInflater) // Inflate the layout using view binding
        setContentView(binding.root)

        // Fetch the current user's ID from Firebase Authentication
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            // Load favorite locations for the current user
            viewModel.loadFavoriteLocations(currentUserId)
        }

        initFavoriteLocations() // Initialize the favorite locations RecyclerView
    }

    /**
     * Initializes the RecyclerView to display the list of favorite locations.
     * Observes the LiveData from the ViewModel and updates the UI when data changes.
     */
    private fun initFavoriteLocations() {
        binding.apply {
            // Show the progress bar while loading data
            progressBarFavoriteLocation.visibility = View.VISIBLE

            // Observe changes in the favorite locations LiveData
            viewModel.favoriteLocations.observe(this@FavoriteLocationListActivity, Observer { favorites ->
                if (favorites.isNullOrEmpty()) {
                    // Log and handle the empty state when no favorite locations are found
                    Log.d(TAG, "No favorite locations found.")
                    // Optionally, display a message to indicate the empty state
                } else {
                    // Log the number of favorite locations loaded
                    Log.d(TAG, "Favorite locations loaded: ${favorites.size}")

                    // Set up the RecyclerView with a LinearLayoutManager and an adapter
                    viewFavoriteLocationList.layoutManager = LinearLayoutManager(
                        this@FavoriteLocationListActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    // Assign the adapter to the RecyclerView
                    viewFavoriteLocationList.adapter = FavoriteLocationAdapter(favorites)
                }
                // Hide the progress bar after data is loaded
                progressBarFavoriteLocation.visibility = View.GONE
            })

            // Set up the back button to close the activity
            backBtn.setOnClickListener {
                finish()
            }
        }
    }
}
