package com.COMP3040.NanjingGo.Activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.COMP3040.NanjingGo.Adapter.LocationAdapter2
import com.COMP3040.NanjingGo.ViewModel.MainViewModel
import com.COMP3040.NanjingGo.databinding.ActivityTopLocationsBinding
import java.util.Calendar

/**
 * TopLocationsActivity displays a list of top-rated locations.
 * This activity utilizes a RecyclerView to show location data and fetches
 * information from the ViewModel, which is connected to Firebase or other data sources.
 */
class TopLocationsActivity : BaseActivity() {
    private lateinit var binding: ActivityTopLocationsBinding // Binding for accessing UI components
    private val viewModel = MainViewModel() // ViewModel to manage location data

    companion object {
        const val TAG = "TopLocationsActivity" // Tag for logging debug information
    }

    /**
     * Called when the activity is created.
     * Initializes the activity and sets up the RecyclerView for displaying locations.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     * being shut down, this contains the data it most recently supplied in onSaveInstanceState.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopLocationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate: Activity Created") // Debug log for activity creation
        initTopLocations() // Initialize the top locations view
    }

    /**
     * Initialize the RecyclerView and load top locations from the ViewModel.
     * Sets up the observer for LiveData and configures the RecyclerView with a layout manager and adapter.
     */
    private fun initTopLocations() {
        binding.apply {
            Log.d(TAG, "initTopLocations: Initializing top locations") // Debug log for initialization

            // Show progress bar while data is being loaded
            progressBarTopLocation.visibility = View.VISIBLE
            viewTopLocationList.visibility = View.VISIBLE // Ensure the RecyclerView is visible

            // Observe changes in the locations LiveData
            viewModel.locations.observe(this@TopLocationsActivity, Observer { locations ->
                Log.d(TAG, "initTopLocations: Locations list updated. Size: ${locations?.size ?: 0}") // Log the size of the list

                if (locations.isNullOrEmpty()) {
                    Log.d(TAG, "initTopLocations: No locations loaded") // Log when no data is available
                    // Optionally, display a message indicating no data is available
                } else {
                    Log.d(TAG, "initTopLocations: Locations loaded successfully") // Log when data is loaded successfully
                }

                // Configure the RecyclerView with a LinearLayoutManager and populate it with an adapter
                viewTopLocationList.layoutManager = LinearLayoutManager(
                    this@TopLocationsActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                viewTopLocationList.adapter = LocationAdapter2(locations) // Set the adapter with the data

                // Hide the progress bar after data is loaded
                progressBarTopLocation.visibility = View.GONE
            })

            Log.d(TAG, "initTopLocations: Loading all locations") // Log for loading all locations
            viewModel.loadAllLocations() // Trigger data loading in the ViewModel

            // Handle back button click to close the activity
            backBtn.setOnClickListener {
                Log.d(TAG, "initTopLocations: Back button clicked") // Log for back button click
                finish()
            }
        }
    }

    /**
     * Determine the current season based on the current month.
     * Seasons are defined as:
     * - Spring: March to May
     * - Summer: June to August
     * - Fall: September to November
     * - Winter: December to February
     *
     * @return A string representing the current season ("Spring", "Summer", "Fall", "Winter").
     */
    private fun getCurrentSeason(): String {
        val month = Calendar.getInstance().get(Calendar.MONTH) + 1 // Months are 0-indexed in Java
        return when (month) {
            in 3..5 -> "Spring" // March to May
            in 6..8 -> "Summer" // June to August
            in 9..11 -> "Fall"   // September to November
            else -> "Winter"    // December to February
        }
    }
}
