package com.openclassrooms.realestatemanager.views.viewholders

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.RealEstateWithMedias
import com.openclassrooms.realestatemanager.utils.MyUtils


class RealEstateViewHolder(realEstateView: View) : RecyclerView.ViewHolder(realEstateView) {
    // Represent a line of a real estate property in the RecyclerView

    private var realEstateType: TextView? = null
    private var realEstateDistrict: TextView? = null
    private var realEstatePrice: TextView? = null
    private var realEstateImage: ImageView? = null
    private var realEstateCardView: CardView? = null
    private val myUtils = MyUtils()

    init {
        realEstateType = itemView.findViewById(R.id.real_estate_type)
        realEstateDistrict = itemView.findViewById(R.id.real_estate_district)
        realEstatePrice = itemView.findViewById(R.id.real_estate_price)
        realEstateImage = itemView.findViewById(R.id.real_estate_image)
        realEstateCardView = itemView.findViewById(R.id.real_estate_card_view)
    }

    fun updateRealEstates(realEstateWithMedias: RealEstateWithMedias?, glide: RequestManager, context: Context) {
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
    }
}
