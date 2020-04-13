package com.openclassrooms.realestatemanager.utils

import android.widget.TextView

class MyUtils {

    fun displayIntegerProperties(value: Int?, text: TextView?) {
        if (value != -1) {
            text?.text = value.toString()
        } else {
            text?.text = ""
        }
    }

}