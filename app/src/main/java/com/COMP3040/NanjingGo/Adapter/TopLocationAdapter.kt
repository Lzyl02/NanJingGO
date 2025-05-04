package com.COMP3040.NanjingGo.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.COMP3040.NanjingGo.Activity.DetailActivity
import com.COMP3040.NanjingGo.Domain.LocationModel
import com.COMP3040.NanjingGo.databinding.ViewholderLocationBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions

/**
 * Adapter for displaying a list of top locations in a RecyclerView.
 *
 * @param items A mutable list of LocationModel objects representing the locations to display.
 */
class TopLocationAdapter(val items: MutableList<LocationModel>) :
    RecyclerView.Adapter<TopLocationAdapter.Viewholder>() {

    private var context: Context? = null // Context to launch activities

    /**
     * ViewHolder class to hold the views for each top location item.
     *
     * @param binding The ViewBinding for the location item layout.
     */
    class Viewholder(val binding: ViewholderLocationBinding) :
        RecyclerView.ViewHolder(binding.root)

    /**
     * Inflates the layout for a location item and creates a ViewHolder.
     *
     * @param parent The parent ViewGroup into which the new view will be added.
     * @param viewType The view type of the new View (unused in this case).
     * @return A new instance of Viewholder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopLocationAdapter.Viewholder {
        context = parent.context // Store the context for launching DetailActivity
        val binding = ViewholderLocationBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    /**
     * Binds the data for a location to the ViewHolder at the specified position.
     *
     * @param holder The ViewHolder to bind the data to.
     * @param position The position of the item in the list.
     */
    override fun onBindViewHolder(holder: TopLocationAdapter.Viewholder, position: Int) {
        val location = items[position]

        // Set the name of the location
        holder.binding.nameTxt.text = location.name

        // Load the location's image using Glide
        Glide.with(holder.itemView.context)
            .load(location.picture) // Load the image from the URL
            .apply { RequestOptions().transform(CenterCrop()) } // Apply a center crop transformation
            .into(holder.binding.img)

        // Set a click listener to navigate to the DetailActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("object", location) // Pass the LocationModel object to DetailActivity
            }
            context?.startActivity(intent) // Launch DetailActivity
        }
    }

    /**
     * Returns the total number of items in the list.
     *
     * @return The size of the items list.
     */
    override fun getItemCount(): Int = items.size
}
