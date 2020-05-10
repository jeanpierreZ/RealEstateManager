package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager

class MyUtils {

    fun displayIntegerProperties(value: Int?, text: TextView?) {
        if (value != -1) {
            text?.text = value.toString()
        } else {
            text?.text = ""
        }
    }

    fun showShortToastMessage(context: Context, int: Int) {
        Toast.makeText(context, int, Toast.LENGTH_SHORT).show()
    }

    //----------------------------------------------------------------------------------
    // Configure DialogFragments

    fun openPOIDialogFragment(editText: EditText, fragmentManager: FragmentManager) {
        val pOIDialogFragment = POIDialogFragment(editText)
        pOIDialogFragment.show(fragmentManager, "pOIDialogFragment")
    }

    fun openPropertyDialogFragment(editText: EditText, title: Int, list: Array<CharSequence>, fragmentManager: FragmentManager) {
        val propertyDialogFragment = PropertyDialogFragment(editText, title, list)
        propertyDialogFragment.show(fragmentManager, "propertyDialogFragment")
    }

    fun openDateDialogFragment(editDate: EditText, fragmentManager: FragmentManager) {
        val dateDialogFragment = DateDialogFragment(editDate)
        dateDialogFragment.show(fragmentManager, "dateDialogFragment")
    }

}