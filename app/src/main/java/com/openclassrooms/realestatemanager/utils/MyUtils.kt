package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.widget.TextView
import android.widget.Toast
import com.openclassrooms.realestatemanager.R

class MyUtils {

    fun displayIntegerProperties(value: Int?, text: TextView?) {
        if (value != -1) {
            text?.text = value.toString()
        } else {
            text?.text = ""
        }
    }

    fun showMessageRealEstateNotSaved(context: Context) {
        Toast.makeText(context, R.string.real_estate_not_saved, Toast.LENGTH_SHORT).show()
    }

    fun showMessageNoLocationForPicture(context: Context) {
        Toast.makeText(context, R.string.no_picture_location, Toast.LENGTH_SHORT).show()
    }

}