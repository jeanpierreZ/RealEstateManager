package com.openclassrooms.realestatemanager.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.MainActivity.Companion.ITEM_POSITION

/**
 * A simple [Fragment] subclass.
 */
class DetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val fragmentView: View = inflater.inflate(R.layout.fragment_details, container, false)

        val textView: TextView = fragmentView.findViewById(R.id.details_fragment_textview)

        val itemPosition: Int? = arguments?.getInt(ITEM_POSITION)
        textView.text = itemPosition.toString()

        return fragmentView
    }

}
