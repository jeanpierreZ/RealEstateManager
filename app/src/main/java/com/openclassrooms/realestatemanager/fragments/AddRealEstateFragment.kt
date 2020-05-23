package com.openclassrooms.realestatemanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.activities.RealEstateActivity

/**
 * A simple [Fragment] subclass.
 */
class AddRealEstateFragment : BaseRealEstateFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the parent layout for this fragment
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val title = arguments?.getString(RealEstateActivity.TITLE_FRAGMENT)
        binding.fragmentBaseRealEstateTitle.text = title

        return view
    }
}
