package com.openclassrooms.realestatemanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.activities.ItemActivity

/**
 * A simple [Fragment] subclass.
 */
class AddItemFragment : BaseItemFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the parent layout for this fragment
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val title = arguments?.getString(ItemActivity.TITLE)
        binding.fragmentBaseItemTitle.text = title

        return view
    }
}
