package com.openclassrooms.realestatemanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.RealEstateWithMedias
import com.openclassrooms.realestatemanager.views.viewholders.RealEstateViewHolder
import kotlinx.android.synthetic.main.real_estate.view.*
import java.lang.ref.WeakReference


class RealEstateAdapter(private var list: List<RealEstateWithMedias?>,
                        private val glide: RequestManager,
                        private var callback: Listener,
                        private var context: Context) : RecyclerView.Adapter<RealEstateViewHolder>() {

    companion object {
        private var lastClickedItemPosition = -1
    }

    // Callback
    interface Listener {
        fun onClickRealEstate(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealEstateViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.real_estate, parent, false)
        return RealEstateViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RealEstateViewHolder, position: Int) {
        holder.updateRealEstates(list[position], glide, context)
        holder.itemView.setOnClickListener {
            // Create a new weak Reference to our Listener
            val callbackWeakRef = WeakReference(callback)
            callback = callbackWeakRef.get()!!
            // Fire the listener to get the real estate position in the list
            callback.onClickRealEstate(position)

            // Update last item clicked position for UI
            lastClickedItemPosition = position
            notifyDataSetChanged()
        }

        // Set accent light color for the real estate selected
        if (lastClickedItemPosition == position) {
            holder.itemView.real_estate_card_view.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentLight))
            holder.itemView.real_estate_price.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
        } else {
            holder.itemView.real_estate_card_view.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
            holder.itemView.real_estate_price.setTextColor(ContextCompat.getColor(context, R.color.colorAccentDark))
        }
    }

    fun setRealEstates(realEstateWithMediasList: List<RealEstateWithMedias?>) {
        list = realEstateWithMediasList
        notifyDataSetChanged()
    }

    // Return the position of a real estate in the list
    fun getPosition(position: Int): RealEstateWithMedias? = list[position]
}
