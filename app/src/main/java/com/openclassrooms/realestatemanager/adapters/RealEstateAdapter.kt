package com.openclassrooms.realestatemanager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.databinding.RealEstateBinding
import com.openclassrooms.realestatemanager.models.RealEstateWithMedias
import com.openclassrooms.realestatemanager.views.viewholders.RealEstateViewHolder


class RealEstateAdapter(private var list: List<RealEstateWithMedias?>,
                        private val glide: RequestManager,
                        private val callback: Listener) : RecyclerView.Adapter<RealEstateViewHolder>() {

    // Callback
    interface Listener {
        fun onClickRealEstate(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealEstateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RealEstateBinding.inflate(inflater, parent, false)
        return RealEstateViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RealEstateViewHolder, position: Int) =
            holder.updateRealEstates(list[position], glide, callback)

    fun setRealEstates(realEstateWithMediasList: List<RealEstateWithMedias?>) {
        list = realEstateWithMediasList
        notifyDataSetChanged()
    }

    // Return the position of a real estate in the list
    fun getPosition(position: Int): RealEstateWithMedias? = list[position]
}
