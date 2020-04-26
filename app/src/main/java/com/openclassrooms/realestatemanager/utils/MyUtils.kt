package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.widget.TextView
import android.widget.Toast

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

}