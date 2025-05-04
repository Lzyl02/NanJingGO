package com.COMP3040.NanjingGo.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.COMP3040.NanjingGo.Activity.DetailActivity
import com.COMP3040.NanjingGo.Domain.LocationModel
import com.COMP3040.NanjingGo.databinding.ViewholderLocation2Binding
import com.google.firebase.database.FirebaseDatabase

/**
 * Adapter for displaying a list of favorite locations in a RecyclerView.
 *
 * @param items A list of LocationModel objects representing the favorite locations.
 */
class FavoriteLocationAdapter(private val items: List<LocationModel>) :
    RecyclerView.Adapter<FavoriteLocationAdapter.ViewHolder>() {

    private var context: Context? = null // Context to be used for launching activities

    /**
     * ViewHolder class to hold the views for each favorite location item.
     *
     * @param binding The ViewBinding for the location item layout.
     */
    class ViewHolder(val binding: ViewholderLocation2Binding) :
        RecyclerView.ViewHolder(binding.root)

    /**
     * Inflates the layout for a favorite location item and creates a ViewHolder.
     *
     * @param parent The parent ViewGroup into which the new view will be added.
     * @param viewType The view type of the new View (unused in this case).
     * @return A new instance of ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context // Store the context for later use
        val binding =
            ViewholderLocation2Binding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    /**
     * Binds the data for a location to the ViewHolder at the specified position.
     *
     * @param holder The ViewHolder to bind the data to.
     * @param position The position of the item in the list.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = items[position]

        // Set the name and default rating for the location
        holder.binding.nameTxt.text = location.name
        holder.binding.ratingTxt.text = "Loading..."
        holder.binding.ratingBar.rating = 0f

        // Fetch the location's rating from Firebase
        val databaseReference = FirebaseDatabase.getInstance().getReference("locations")
        databaseReference.addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                for (locationSnapshot in snapshot.children) {
                    val name = locationSnapshot.child("name").getValue(String::class.java)
                    if (name?.trim() == location.name?.trim()) {
                        // Update the UI with the retrieved rating
                        val rating = locationSnapshot.child("rating").getValue(String::class.java)
                            ?.replace("/5", "")?.toFloatOrNull() ?: 0f
                        holder.binding.ratingTxt.text = String.format("%.1f", rating)
                        holder.binding.ratingBar.rating = rating
                        Log.d("FavoriteLocationAdapter", "Fetched rating: $rating for ${location.name}")
                        return
                    }
                }
                // Fallback for when no rating is found
                holder.binding.ratingTxt.text = "N/A"
                holder.binding.ratingBar.rating = 0f
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                // Log the error and show fallback data
                Log.e(
                    "FavoriteLocationAdapter",
                    "Failed to fetch rating for ${location.name}",
                    error.toException()
                )
                holder.binding.ratingTxt.text = "N/A"
                holder.binding.ratingBar.rating = 0f
            }
        })

        // Load the location's image using Glide
        Glide.with(holder.itemView.context)
            .load(location.picture) // Load the image from the provided URL
            .apply(RequestOptions().transform(CenterCrop())) // Apply a center crop transformation
            .into(holder.binding.img)

        // Set up a click listener for the "Make" button to navigate to DetailActivity
        holder.binding.makeBtn.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("object", location) // Pass the LocationModel object to the DetailActivity
            }
            context?.startActivity(intent) // Launch DetailActivity
        }
    }

    /**
     * Returns the total number of items in the list.
     *
     * @return The size of the items list.
     */
    override fun getItemCount(): Int {
        return items.size
    }
}
