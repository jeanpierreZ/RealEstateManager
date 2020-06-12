package com.openclassrooms.realestatemanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.RealEstateWithMedias
import com.openclassrooms.realestatemanager.views.viewholders.RealEstateViewHolder


class RealEstateAdapter(private var list: List<RealEstateWithMedias?>,
                        private val glide: RequestManager,
                        private val callback: Listener,
private var context: Context) : RecyclerView.Adapter<RealEstateViewHolder>() {

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

    override fun onBindViewHolder(holder: RealEstateViewHolder, position: Int) =
            holder.updateRealEstates(list[position], glide, callback, context)

    fun setRealEstates(realEstateWithMediasList: List<RealEstateWithMedias?>) {
        list = realEstateWithMediasList
        notifyDataSetChanged()
    }

    // Return the position of a real estate in the list
    fun getPosition(position: Int): RealEstateWithMedias? = list[position]
}
