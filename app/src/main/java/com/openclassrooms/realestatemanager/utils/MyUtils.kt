package com.openclassrooms.realestatemanager.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
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

    fun showSnackbarMessage(activity: Activity?, text: String?) {
        val snackbar = activity?.findViewById<View>(android.R.id.content)?.let { Snackbar.make(it, text!!, 3500) }
        snackbar?.view?.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorWhite))
        snackbar?.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark))
        snackbar?.view?.setOnClickListener { _ -> snackbar.dismiss() }
        snackbar?.show()
    }

    fun getScreenWidth(activity: Activity): Float {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels.coerceAtMost(displayMetrics.heightPixels) / displayMetrics.density
    }

    fun addPagerToRecyclerView(context: Context, list: ArrayList<Media?>, position: Int, linearLayout: LinearLayout) {
        linearLayout.removeAllViews()
        val infiniteMiddlePageList = arrayOf(1, 2, 3, 4, 5)

        if (list.size >= 8) {

            // Set parameters for the fist page
            val infiniteFirstPage = View(context)
            linearLayout.addView(infiniteFirstPage)

            infiniteFirstPage.updateLayoutParams<ViewGroup.MarginLayoutParams> { setMargins(5, 0, 5, 0) }
            if (position == 0) {
                infiniteFirstPage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                infiniteFirstPage.layoutParams.height = 10
                infiniteFirstPage.layoutParams.width = 40
            } else {
                infiniteFirstPage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                infiniteFirstPage.layoutParams.height = 5
                infiniteFirstPage.layoutParams.width = 20
            }

            // Set parameters for middle pages
            for (page in infiniteMiddlePageList) {
                val infiniteMiddlePage = View(context)
                linearLayout.addView(infiniteMiddlePage)

                infiniteMiddlePage.layoutParams.height = 10
                infiniteMiddlePage.layoutParams.width = 40
                infiniteMiddlePage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                infiniteMiddlePage.updateLayoutParams<ViewGroup.MarginLayoutParams> { setMargins(5, 0, 5, 0) }

                if (position > 3 && position < (list.lastIndex - 2) && page == 3) {
                    infiniteMiddlePage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                }
                when (page) {
                    1 -> if (page == position) infiniteMiddlePage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                    2 -> if (page == position) infiniteMiddlePage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                    3 -> if (page == position) infiniteMiddlePage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                    4 -> if (position == (list.lastIndex - 2)) infiniteMiddlePage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                    5 -> if (position == (list.lastIndex - 1)) infiniteMiddlePage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                }
            }

            // Set parameters for the last page
            val infiniteLastPage = View(context)
            linearLayout.addView(infiniteLastPage)

            infiniteLastPage.updateLayoutParams<ViewGroup.MarginLayoutParams> { setMargins(5, 0, 5, 0) }
            if (position == list.lastIndex) {
                infiniteLastPage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                infiniteLastPage.layoutParams.height = 10
                infiniteLastPage.layoutParams.width = 40
            } else {
                infiniteLastPage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                infiniteLastPage.layoutParams.height = 5
                infiniteLastPage.layoutParams.width = 20
            }

            // Add a view for each media when the list size is < than 8
        } else {
            for (media in 0 until list.size) {
                val pagerView = View(context)
                // Add view to the LinearLayout
                linearLayout.addView(pagerView)
                // Set parameters
                pagerView.layoutParams.height = 10
                pagerView.layoutParams.width = 40
                pagerView.updateLayoutParams<ViewGroup.MarginLayoutParams> { setMargins(5, 0, 5, 0) }
                // Distinguish the actual position in the RecyclerView by colorAccent
                if (media == position) {
                    pagerView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                } else {
                    pagerView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                }
            }
        }
    }


    //----------------------------------------------------------------------------------
    // Configure DialogFragments

    fun openPOIDialogFragment(editText: EditText, previouslySelectedItems: ArrayList<String>?, fragmentManager: FragmentManager) {
        val pOIDialogFragment = POIDialogFragment(editText, previouslySelectedItems)
        pOIDialogFragment.show(fragmentManager, "pOIDialogFragment")
    }

    fun openPropertyDialogFragment(editText: EditText, title: Int, previouslySelectedChoice: String?,
                                   list: Array<CharSequence>, fragmentManager: FragmentManager) {
        val propertyDialogFragment = PropertyDialogFragment(editText, title, previouslySelectedChoice, list)
        propertyDialogFragment.show(fragmentManager, "propertyDialogFragment")
    }

    fun openDateDialogFragment(editDate: EditText, previousDate: String?, fragmentManager: FragmentManager) {
        val dateDialogFragment = DateDialogFragment(editDate, previousDate)
        dateDialogFragment.show(fragmentManager, "dateDialogFragment")
    }

}
