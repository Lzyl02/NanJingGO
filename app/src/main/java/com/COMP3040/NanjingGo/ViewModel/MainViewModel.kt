package com.COMP3040.NanjingGo.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.COMP3040.NanjingGo.Domain.LocationModel
import java.util.Calendar

/**
 * ViewModel for managing and retrieving location-related data.
 */
class MainViewModel : ViewModel() {

    private val firebaseDatabase = FirebaseDatabase.getInstance() // Firebase database instance
    private val _locations = MutableLiveData<MutableList<LocationModel>>() // LiveData for location data
    val locations: LiveData<MutableList<LocationModel>> = _locations // Exposed LiveData for observers

    companion object {
        const val TAG = "MainViewModel" // Tag for logging
    }

    /**
     * Loads locations from Firebase filtered by a specific season.
     *
     * @param season The season to filter locations (e.g., "Spring", "Autumn").
     */
    fun loadLocations(season: String) {
        val ref = firebaseDatabase.getReference("locations") // Reference to "locations" in Firebase

        Log.d(TAG, "loadLocations: Attempting to fetch locations for season: $season")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "onDataChange: Data snapshot exists with ${snapshot.childrenCount} children.")
                    val locationsList = mutableListOf<LocationModel>()

                    // Iterate over all locations in the snapshot
                    for (childSnapshot in snapshot.children) {
                        val locationMap = childSnapshot.value as? Map<String, Any?>
                        if (locationMap != null) {
                            // Check if the location matches the desired season
                            val bestSeasons = locationMap["best_season"] as? String
                            if (bestSeasons?.contains(season, ignoreCase = true) == true) {
                                val location = LocationModel(
                                    name = locationMap["name"] as? String ?: "",
                                    address = locationMap["address"] as? String ?: "",
                                    phone = locationMap["phone"] as? String ?: "",
                                    website = locationMap["website"] as? String,
                                    description = locationMap["description"] as? String ?: "",
                                    openingTime = locationMap["opening_time"] as? String ?: "",
                                    rating = (locationMap["rating"] as? String)?.toDoubleOrNull() ?: 0.0,
                                    suggestedDuration = locationMap["suggested_duration"] as? String ?: "",
                                    bestSeason = locationMap["best_season"] as? String ?: "",
                                    picture = locationMap["picture"] as? String,
                                    ticket = null, // Tickets are not parsed here
                                    travelTips = locationMap["travel_tips"] as? List<String> ?: listOf()
                                )
                                locationsList.add(location)
                                Log.d(TAG, "onDataChange: Location added for season $season: ${location.name}")
                            }
                        }
                    }

                    _locations.value = locationsList
                    Log.d(TAG, "onDataChange: Total locations for $season: ${locationsList.size}")
                } else {
                    Log.d(TAG, "onDataChange: No data found at reference.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: Failed to load locations. Error: ${error.message}")
            }
        })
    }

    /**
     * Loads all locations from Firebase without any filtering.
     */
    fun loadAllLocations() {
        val ref = firebaseDatabase.getReference("locations") // Reference to "locations" in Firebase

        Log.d(TAG, "loadAllLocations: Attempting to fetch all locations.")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "onDataChange: Data snapshot exists with ${snapshot.childrenCount} children.")
                    val locationsList = mutableListOf<LocationModel>()

                    // Iterate over all locations in the snapshot
                    for (childSnapshot in snapshot.children) {
                        val locationMap = childSnapshot.value as? Map<String, Any?>
                        if (locationMap != null) {
                            val location = LocationModel(
                                name = locationMap["name"] as? String ?: "",
                                address = locationMap["address"] as? String ?: "",
                                phone = locationMap["phone"] as? String ?: "",
                                website = locationMap["website"] as? String,
                                description = locationMap["description"] as? String ?: "",
                                openingTime = locationMap["opening_time"] as? String ?: "",
                                rating = (locationMap["rating"] as? String)?.toDoubleOrNull() ?: 0.0,
                                suggestedDuration = locationMap["suggested_duration"] as? String ?: "",
                                bestSeason = locationMap["best_season"] as? String ?: "",
                                picture = locationMap["picture"] as? String,
                                ticket = null, // Tickets are not parsed here
                                travelTips = locationMap["travel_tips"] as? List<String> ?: listOf()
                            )
                            locationsList.add(location)
                            Log.d(TAG, "onDataChange: Location added: ${location.name}")
                        }
                    }

                    _locations.value = locationsList
                    Log.d(TAG, "onDataChange: Total locations loaded: ${locationsList.size}")
                } else {
                    Log.d(TAG, "onDataChange: No data found at reference.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: Failed to load locations. Error: ${error.message}")
            }
        })
    }

    // LiveData for the user's favorite locations
    val favoriteLocations = MutableLiveData<List<LocationModel>>()

    /**
     * Loads the user's favorite locations from Firebase.
     *
     * @param userId The user's unique ID in Firebase.
     */
    fun loadFavoriteLocations(userId: String) {
        Log.d(TAG, "loadFavoriteLocations: Attempting to load favorite locations for userId: $userId")

        val databaseReference = firebaseDatabase.getReference("users").child(userId).child("favoriteLocations")

        databaseReference.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    Log.d(TAG, "loadFavoriteLocations: Snapshot exists. Parsing data...")

                    // Parse favorite locations from the snapshot
                    val favorites = snapshot.children.mapNotNull { childSnapshot ->
                        val location = childSnapshot.getValue(LocationModel::class.java)
                        if (location == null) {
                            Log.w(TAG, "loadFavoriteLocations: Found a null or invalid location entry in snapshot.")
                        }
                        location
                    }

                    Log.d(TAG, "loadFavoriteLocations: Successfully loaded ${favorites.size} favorite locations.")
                    favoriteLocations.postValue(favorites) // Update the LiveData
                } else {
                    Log.d(TAG, "loadFavoriteLocations: No favorite locations found in snapshot.")
                    favoriteLocations.postValue(emptyList()) // Update with an empty list
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "loadFavoriteLocations: Failed to load favorite locations. Error: ${exception.message}")
                favoriteLocations.postValue(emptyList()) // Update with an empty list on failure
            }
    }
}
