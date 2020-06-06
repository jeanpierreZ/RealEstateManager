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

        myUtils.displayIntegerProperties(price, fragment_base_real_estate_edit_price)
        myUtils.displayIntegerProperties(surface, fragment_base_real_estate_edit_surface)
        myUtils.displayIntegerProperties(roomsNumber, fragment_base_real_estate_edit_rooms)
        myUtils.displayIntegerProperties(bathroomsNumber, fragment_base_real_estate_edit_bathrooms)
        myUtils.displayIntegerProperties(bedroomsNumber, fragment_base_real_estate_edit_bedrooms)

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set data of the real estate from DetailsFragment
        val title = arguments?.getString(RealEstateActivity.TITLE_FRAGMENT)
        fragment_base_real_estate_title.text = title
    }
}
