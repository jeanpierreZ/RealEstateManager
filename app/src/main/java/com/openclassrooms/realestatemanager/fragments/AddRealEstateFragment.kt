package com.openclassrooms.realestatemanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.activities.RealEstateActivity
import com.openclassrooms.realestatemanager.utils.MyUtils
import kotlinx.android.synthetic.main.fragment_base_real_estate.*

/**
 * A simple [Fragment] subclass.
 */
class AddRealEstateFragment : BaseRealEstateFragment() {

    private val myUtils = MyUtils()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = super.onCreateView(inflater, container, savedInstanceState)

        myUtils.displayIntegerProperties(price, fragment_base_real_estate_price?.editText)
        myUtils.displayIntegerProperties(surface, fragment_base_real_estate_surface?.editText)
        myUtils.displayIntegerProperties(roomsNumber, fragment_base_real_estate_rooms?.editText)
        myUtils.displayIntegerProperties(bathroomsNumber, fragment_base_real_estate_bathrooms?.editText)
        myUtils.displayIntegerProperties(bedroomsNumber, fragment_base_real_estate_bedrooms?.editText)

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set data of the real estate from DetailsFragment
        val title = arguments?.getString(RealEstateActivity.TITLE_FRAGMENT)
        fragment_base_real_estate_title.text = title
    }
}
