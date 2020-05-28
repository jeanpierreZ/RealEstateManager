package com.openclassrooms.realestatemanager.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Media
import com.openclassrooms.realestatemanager.utils.dialogfragments.DateDialogFragment
import com.openclassrooms.realestatemanager.utils.dialogfragments.POIDialogFragment
import com.openclassrooms.realestatemanager.utils.dialogfragments.PropertyDialogFragment

class MyUtils {

    // For properties of the real estate that have a negative value if they are null
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

    fun getScreenWidth(activity: Activity): Float {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels.coerceAtMost(displayMetrics.heightPixels) / displayMetrics.density
    }

    fun addPagerToRecyclerView(context: Context, list: ArrayList<Media?>, position: Int, linearLayout: LinearLayout) {
        linearLayout.removeAllViews()
        // Add a view for each media in list
        for (media in 0 until list.size) {
            val pagerView = View(context)
            // Add view to the LinearLayout
            linearLayout.addView(pagerView)
            // Set parameters
            pagerView.layoutParams.height = 10
            pagerView.layoutParams.width = 20
            pagerView.updateLayoutParams<ViewGroup.MarginLayoutParams> { setMargins(5, 0, 5, 0) }
            // Distinguish the actual position in the RecyclerView by colorAccent
            if (media == position) {
                pagerView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
            } else {
                pagerView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }
        }
    }

    //----------------------------------------------------------------------------------
    // Configure DialogFragments

    fun openPOIDialogFragment(editText: EditText, previouslySelectedItems: ArrayList<String>?, fragmentManager: FragmentManager) {
        val pOIDialogFragment = POIDialogFragment(editText, previouslySelectedItems)
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