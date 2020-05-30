package com.openclassrooms.realestatemanager.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.activities.RealEstateActivity
import kotlinx.android.synthetic.main.fragment_base_real_estate.*

/**
 * A simple [Fragment] subclass.
 */
class AddRealEstateFragment : BaseRealEstateFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set data of the real estate from DetailsFragment
        val title = arguments?.getString(RealEstateActivity.TITLE_FRAGMENT)
        fragment_base_real_estate_title.text = title
    }
}
