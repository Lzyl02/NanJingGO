package com.COMP3040.NanjingGo.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.COMP3040.NanjingGo.Domain.LocationModel
import com.COMP3040.NanjingGo.R
import com.COMP3040.NanjingGo.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

/**
 * Activity for displaying detailed information about a location.
 *
 * This activity allows users to view details about a specific location, including its name,
 * address, description, rating, best season to visit, and suggested duration. It also provides
 * functionalities for navigating to the location, visiting its website, making a call, sharing
 * the location details, viewing tips, and marking the location as a favorite.
 */
class DetailActivity : BaseActivity() {

    /**
     * Binding object for the layout.
     */
    private lateinit var binding: ActivityDetailBinding

    /**
     * Data object representing the location details.
     */
    private lateinit var item: LocationModel

    /**
     * Firebase authentication instance for retrieving the current user's ID.
     */
    private val auth = FirebaseAuth.getInstance()

    /**
     * Firebase database reference for accessing the user's data.
     */
    private val databaseReference = FirebaseDatabase.getInstance().getReference("users")

    /**
     * Boolean to track if the location is marked as a favorite.
     */
    private var isFavorite: Boolean = false

    /**
     * Called when the activity is created.
     *
     * Initializes the view binding, sets the content view, and loads the location details
     * passed through the intent.
     *
     * @param savedInstanceState If the activity is being reinitialized after being
     * previously shut down, this Bundle contains the data it most recently supplied.
     * Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBundle() // Load location data from intent
    }

    /**
     * Extracts data passed through the intent and sets up the UI with location details.
     */
    private fun getBundle() {
        item = intent.getParcelableExtra("object")!!

        binding.apply {
            titleTxt.text = item.name
            addressTxt.text = item.address
            descriptionTxt.text = item.description
            specialTxt.text = item.bestSeason
            suggestedDurationTxt.text = item.suggestedDuration
            openingtimeTxt.text = item.openingTime ?: "N/A"

            loadRating() // Load the location rating from Firebase

            // Setup button click listeners
            websiteBtn.setOnClickListener { openWebsite() }
            callBtn.setOnClickListener { makeCall() }
            shareBtn.setOnClickListener { shareLocation() }
            directionBtn.setOnClickListener { navigateToLocation() }
            backBtn.setOnClickListener { finish() }
            favBtn.setOnClickListener { toggleFavorite() }
            messageBtn.setOnClickListener { openTips() }

            // Load image using Glide
            Glide.with(this@DetailActivity)
                .load(item.picture)
                .into(img)

            checkIfFavorite() // Check if the location is already marked as favorite
        }
    }

    /**
     * Loads the rating of the location from Firebase and updates the UI.
     */
    private fun loadRating() {
        val locationsRef = FirebaseDatabase.getInstance().getReference("locations")

        locationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (locationSnapshot in snapshot.children) {
                    val name = locationSnapshot.child("name").getValue(String::class.java)
                    if (name == item.name) {
                        val rating = locationSnapshot.child("rating").getValue(String::class.java)
                        binding.ratingTxt.text = rating ?: "N/A"
                        return
                    }
                }
                binding.ratingTxt.text = "N/A" // Default if no match found
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DetailActivity", "Failed to read locations", error.toException())
                binding.ratingTxt.text = "N/A"
            }
        })
    }

    /**
     * Opens the location's website in a browser.
     */
    private fun openWebsite() {
        item.website?.let {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
            startActivity(intent)
        }
    }

    /**
     * Opens the phone dialer with the location's phone number.
     */
    private fun makeCall() {
        val uri = "tel:${item.phone.trim()}"
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(uri))
        startActivity(intent)
    }

    /**
     * Shares the location details via other apps.
     */
    private fun shareLocation() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out ${item.name}")
            putExtra(
                Intent.EXTRA_TEXT,
                "${item.name}\nAddress: ${item.address}\nPhone: ${item.phone}\nWebsite: ${item.website ?: "N/A"}"
            )
        }
        startActivity(Intent.createChooser(intent, "Share via"))
    }

    /**
     * Opens the map application for navigating to the location's address.
     */
    private fun navigateToLocation() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("geo:0,0?q=${item.address}")
        }
        startActivity(intent)
    }

    /**
     * Opens the TipsActivity to display tips for the location.
     */
    private fun openTips() {
        val intent = Intent(this, TipsActivity::class.java).apply {
            putExtra("locationName", item.name)
        }
        startActivity(intent)
    }

    /**
     * Toggles the favorite status of the location.
     */
    private fun toggleFavorite() {
        if (isFavorite) {
            removeFromFavorites()
        } else {
            addToFavorites()
        }
    }

    /**
     * Adds the location to the user's favorite list in Firebase.
     */
    private fun addToFavorites() {
        val userId = auth.currentUser?.uid ?: return
        val favoriteReference = databaseReference.child(userId).child("favoriteLocations")

        favoriteReference.get().addOnSuccessListener { snapshot ->
            val locationExists = snapshot.children.any {
                val location = it.getValue(LocationModel::class.java)
                location?.name == item.name
            }

            if (locationExists) {
                Toast.makeText(this, "${item.name} is already in your favorites!", Toast.LENGTH_SHORT).show()
            } else {
                val locationId = favoriteReference.push().key
                locationId?.let {
                    favoriteReference.child(it).setValue(item).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            isFavorite = true
                            updateFavoriteButton()
                            Toast.makeText(this, "${item.name} added to favorites!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    /**
     * Removes the location from the user's favorite list in Firebase.
     */
    private fun removeFromFavorites() {
        val userId = auth.currentUser?.uid ?: return
        val favoriteReference = databaseReference.child(userId).child("favoriteLocations")

        favoriteReference.get().addOnSuccessListener { snapshot ->
            val favoriteEntry = snapshot.children.find {
                val location = it.getValue(LocationModel::class.java)
                location?.name == item.name
            }

            favoriteEntry?.ref?.removeValue()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isFavorite = false
                    updateFavoriteButton()
                    Toast.makeText(this, "${item.name} removed from favorites!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Checks if the location is marked as favorite in Firebase.
     */
    private fun checkIfFavorite() {
        val userId = auth.currentUser?.uid ?: return
        val favoriteReference = databaseReference.child(userId).child("favoriteLocations")

        favoriteReference.get().addOnSuccessListener { snapshot ->
            isFavorite = snapshot.children.any {
                val location = it.getValue(LocationModel::class.java)
                location?.name == item.name
            }
            updateFavoriteButton()
        }
    }

    /**
     * Updates the favorite button UI based on the favorite status.
     */
    private fun updateFavoriteButton() {
        binding.favBtn.setImageResource(if (isFavorite) R.drawable.favorite_bold else R.drawable.favorite_white)
    }
}
