package com.openclassrooms.realestatemanager.views.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.adapters.RealEstateAdapter
import com.openclassrooms.realestatemanager.databinding.RealEstateBinding
import com.openclassrooms.realestatemanager.models.RealEstateWithMedias
import com.openclassrooms.realestatemanager.utils.MyUtils
import java.lang.ref.WeakReference


class RealEstateViewHolder(val binding: RealEstateBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    // Represent a line of a real estate property in the RecyclerView

    private val myUtils = MyUtils()

    // For data
    private var callbackWeakRef: WeakReference<RealEstateAdapter.Listener>? = null

    override fun onClick(view: View?) {
        // When a click happens, we fire our listener to get the real estate position in the list
        val callback: RealEstateAdapter.Listener? = callbackWeakRef?.get()
        callback?.onClickRealEstate(adapterPosition)
    }

    fun updateRealEstates(realEstateWithMedias: RealEstateWithMedias?, glide: RequestManager, callback: RealEstateAdapter.Listener) {
        // Update widgets
        binding.realEstateType.text = realEstateWithMedias?.realEstate?.type
        binding.realEstateDistrict.text = realEstateWithMedias?.realEstate?.address?.district
        myUtils.displayIntegerProperties(realEstateWithMedias?.realEstate?.price, binding.realEstatePrice)

        if (realEstateWithMedias?.medias?.size != 0) {
            binding.realEstateImage.let { glide.load(realEstateWithMedias?.medias?.get(0)?.mediaPicture).into(it) }
        }
        // Create a new weak Reference to our Listener
        callbackWeakRef = WeakReference(callback)
        // Implement Listener
        itemView.setOnClickListener(this)
    }
}
