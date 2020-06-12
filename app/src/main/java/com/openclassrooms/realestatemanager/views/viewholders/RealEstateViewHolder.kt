package com.openclassrooms.realestatemanager.views.viewholders

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.RealEstateAdapter
import com.openclassrooms.realestatemanager.models.RealEstateWithMedias
import com.openclassrooms.realestatemanager.utils.MyUtils
import java.lang.ref.WeakReference


class RealEstateViewHolder(realEstateView: View) : RecyclerView.ViewHolder(realEstateView), View.OnClickListener {
    // Represent a line of a real estate property in the RecyclerView

    private var realEstateType: TextView? = null
    private var realEstateDistrict: TextView? = null
    private var realEstatePrice: TextView? = null
    private var realEstateImage: ImageView? = null
    private val myUtils = MyUtils()

    // For data
    private var callbackWeakRef: WeakReference<RealEstateAdapter.Listener>? = null

    init {
        realEstateType = itemView.findViewById(R.id.real_estate_type)
        realEstateDistrict = itemView.findViewById(R.id.real_estate_district)
        realEstatePrice = itemView.findViewById(R.id.real_estate_price)
        realEstateImage = itemView.findViewById(R.id.real_estate_image)
    }

    override fun onClick(view: View?) {
        // When a click happens, we fire our listener to get the real estate position in the list
        val callback: RealEstateAdapter.Listener? = callbackWeakRef?.get()
        callback?.onClickRealEstate(adapterPosition)
    }

    fun updateRealEstates(realEstateWithMedias: RealEstateWithMedias?, glide: RequestManager, callback: RealEstateAdapter.Listener, context: Context) {
        // Update widgets
        realEstateType?.text = realEstateWithMedias?.realEstate?.type
        realEstateDistrict?.text = realEstateWithMedias?.realEstate?.address?.district
        myUtils.displayPrice(realEstateWithMedias?.realEstate?.price, realEstatePrice, context)

        // Necessary if the user don't have a media with the real estate
        if (realEstateWithMedias?.medias?.size != 0) {
            if (realEstateWithMedias?.medias?.get(0)?.mediaPicture?.isAbsolute!!) {
                realEstateImage?.let { glide.load(realEstateWithMedias.medias[0].mediaPicture).into(it) }
            } else if (realEstateWithMedias.medias[0].mediaVideo?.isAbsolute!!) {
                realEstateImage?.let { glide.load(realEstateWithMedias.medias[0].mediaVideo).into(it) }
            }
        } else {
            realEstateImage?.let { glide.clear(it) }
        }

        // Create a new weak Reference to our Listener
        callbackWeakRef = WeakReference(callback)
        // Implement Listener
        itemView.setOnClickListener(this)
    }
}
