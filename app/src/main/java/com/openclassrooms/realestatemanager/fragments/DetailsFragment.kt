package com.openclassrooms.realestatemanager.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.MainActivity.Companion.BUNDLE_ITEM
import com.openclassrooms.realestatemanager.models.Item

/**
 * A simple [Fragment] subclass.
 */
class DetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val fragmentView: View = inflater.inflate(R.layout.fragment_details, container, false)

        val descriptionText: TextView = fragmentView.findViewById(R.id.details_fragment_description)
        val surfaceText: TextView = fragmentView.findViewById(R.id.details_fragment_surface)
        val roomText: TextView = fragmentView.findViewById(R.id.details_fragment_rooms)
        val bathroomText: TextView = fragmentView.findViewById(R.id.details_fragment_bathrooms)
        val bedroomText: TextView = fragmentView.findViewById(R.id.details_fragment_bedrooms)
        val streetText: TextView = fragmentView.findViewById(R.id.details_fragment_street)
        val apartmentText: TextView = fragmentView.findViewById(R.id.details_fragment_apartment)
        val cityText: TextView = fragmentView.findViewById(R.id.details_fragment_city)
        val postalCodeText: TextView = fragmentView.findViewById(R.id.details_fragment_postal_code)
        val countryText: TextView = fragmentView.findViewById(R.id.details_fragment_country)

        // Recover the item from the bundle
        val item: Item? = arguments?.getParcelable(BUNDLE_ITEM)

        descriptionText.text = item?.description
        surfaceText.text = item?.surface.toString()
        roomText.text = item?.roomsNumber.toString()
        bathroomText.text = item?.bathroomsNumber.toString()
        bedroomText.text = item?.bedroomsNumber.toString()
        streetText.text = item?.address
        apartmentText.text = "apart"
        cityText.text = "city"
        postalCodeText.text = "postal code"
        countryText.text = " country"

        return fragmentView
    }

}
