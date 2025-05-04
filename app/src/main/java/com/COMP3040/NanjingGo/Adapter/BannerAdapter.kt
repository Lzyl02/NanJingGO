package com.COMP3040.NanjingGo.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.COMP3040.NanjingGo.R

/**
 * Adapter for displaying a list of banners in a RecyclerView.
 *
 * @param banners A list of drawable resource IDs representing the banners.
 */
class BannerAdapter(private val banners: List<Int>) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    /**
     * ViewHolder class to hold the views for each banner item.
     *
     * @param itemView The root view of the banner item layout.
     */
    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.bannerImage) // ImageView for displaying the banner image
    }

    /**
     * Inflates the layout for a banner item and creates a ViewHolder.
     *
     * @param parent The parent ViewGroup into which the new view will be added.
     * @param viewType The view type of the new View (unused in this case).
     * @return A new instance of BannerViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    /**
     * Binds a banner image to the ViewHolder at the specified position.
     *
     * @param holder The ViewHolder to bind the data to.
     * @param position The position of the item in the list.
     */
    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.imageView.setImageResource(banners[position]) // Set the banner image resource
    }

    /**
     * Returns the total number of items in the banners list.
     *
     * @return The size of the banners list.
     */
    override fun getItemCount(): Int = banners.size
}
