package com.openclassrooms.realestatemanager.utils

import android.app.Activity
import android.content.Context
import android.text.TextWatcher
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
import com.google.android.material.textfield.TextInputLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Media
import com.openclassrooms.realestatemanager.utils.dialogfragments.DateDialogFragment
import com.openclassrooms.realestatemanager.utils.dialogfragments.POIDialogFragment
import com.openclassrooms.realestatemanager.utils.dialogfragments.PropertyDialogFragment
import java.text.NumberFormat


class MyUtils {

    // For properties of the real estate that have a negative value if they are null
    fun displayIntegerProperties(value: Int?, text: TextView?) {
        if (value != -1) {
            text?.text = value.toString()
        } else {
            text?.text = ""
        }
    }

    //----------------------------------------------------------------------------------
    // For DetailsFragment and ViewHolders

    // For the price of a real estate (that has a negative value if it is null)
    fun displayPrice(value: Int?, text: TextView?, context: Context) {
        val formatValue = NumberFormat.getInstance().format(value)
        if (value != -1) {
            text?.text = context.getString(R.string.dollar_price, formatValue.toString())
        } else {
            text?.text = ""
        }
    }

    // For the price of a real estate (that has a negative value if it is null)
    fun displaySurface(value: Int?, text: TextView?, context: Context) {
        if (value != -1) {
            text?.text = context.getString(R.string.sq_m, value)
        } else {
            text?.text = ""
        }
    }

    // For the price of a real estate (that has a negative value if it is null)
    fun displayRooms(value: Int?, text: TextView?, context: Context) {
        if (value != -1) {
            text?.text = value?.let { context.resources.getQuantityString(R.plurals.rooms, it, it) }
        } else {
            text?.text = ""
        }
    }

    // For the price of a real estate (that has a negative value if it is null)
    fun displayBathrooms(value: Int?, text: TextView?, context: Context) {
        if (value != -1) {
            text?.text = value?.let { context.resources.getQuantityString(R.plurals.bathrooms, it, it) }
        } else {
            text?.text = ""
        }
    }

    // For the price of a real estate (that has a negative value if it is null)
    fun displayBedrooms(value: Int?, text: TextView?, context: Context) {
        if (value != -1) {
            text?.text = value?.let { context.resources.getQuantityString(R.plurals.bedrooms, it, it) }
        } else {
            text?.text = ""
        }
    }

    //----------------------------------------------------------------------------------
    // Format price number and validate it

    fun formatPrice(editText: EditText?, textWatcher: TextWatcher, s: CharSequence?): Int? {
        editText?.removeTextChangedListener(textWatcher)

        var price: Int? = null

        try {
            var originalString = s.toString()

            if (originalString.contains(",")) {
                originalString = originalString.replace(",", "")
            }
            val int: Int = originalString.toInt()
            val formatValue = NumberFormat.getInstance().format(int)
            val formattedString = formatValue.toString()

            // Set text formatted in EditText and set price data
            editText?.setText(formattedString)
            editText?.text?.length?.let { editText.setSelection(it) }
            price = originalString.toIntOrNull()
        } catch (nfe: NumberFormatException) {
            nfe.printStackTrace()
        }
        editText?.addTextChangedListener(textWatcher)

        return price
    }

    fun validatePrice(textInputLayout: TextInputLayout, context: Context, s: String): Boolean {
        val filtered = s.filterNot { it == ',' }
        return if (filtered.length > 10) {
            textInputLayout.error = context.getString(R.string.price_to_long)
            false
        } else {
            textInputLayout.error = null
            true
        }
    }

    //----------------------------------------------------------------------------------

    fun showSnackbarMessage(activity: Activity?, text: Int?) {
        val snackbar = activity?.findViewById<View>(android.R.id.content)?.let { Snackbar.make(it, text!!, 3500) }
        snackbar?.view?.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorWhite))
        snackbar?.setTextColor(ContextCompat.getColor(activity, R.color.colorBlack))
        snackbar?.view?.setOnClickListener { _ -> snackbar.dismiss() }
        snackbar?.show()
    }

    fun getScreenWidth(activity: Activity): Float {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels.coerceAtMost(displayMetrics.heightPixels) / displayMetrics.density
    }

    //----------------------------------------------------------------------------------
    fun addPagerToRecyclerView(context: Context, list: ArrayList<Media?>, position: Int, linearLayout: LinearLayout) {
        linearLayout.removeAllViews()
        val infiniteMiddlePageList = arrayOf(1, 2, 3, 4, 5)

        if (list.size >= 8) {

            // Set parameters for the fist page
            val infiniteFirstPage = View(context)
            linearLayout.addView(infiniteFirstPage)

            infiniteFirstPage.updateLayoutParams<ViewGroup.MarginLayoutParams> { setMargins(5, 0, 5, 0) }
            if (position == 0) {
                infiniteFirstPage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentLight))
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
                    infiniteMiddlePage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentLight))
                }
                when (page) {
                    1 -> if (page == position) infiniteMiddlePage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentLight))
                    2 -> if (page == position) infiniteMiddlePage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentLight))
                    3 -> if (page == position) infiniteMiddlePage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentLight))
                    4 -> if (position == (list.lastIndex - 2)) infiniteMiddlePage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentLight))
                    5 -> if (position == (list.lastIndex - 1)) infiniteMiddlePage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentLight))
                }
            }

            // Set parameters for the last page
            val infiniteLastPage = View(context)
            linearLayout.addView(infiniteLastPage)

            infiniteLastPage.updateLayoutParams<ViewGroup.MarginLayoutParams> { setMargins(5, 0, 5, 0) }
            if (position == list.lastIndex) {
                infiniteLastPage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentLight))
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
                    pagerView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentLight))
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
                                   list: Array<CharSequence>, fragmentManager: FragmentManager, toSearch: Boolean) {
        val propertyDialogFragment = PropertyDialogFragment(editText, title, previouslySelectedChoice, list, toSearch)
        propertyDialogFragment.show(fragmentManager, "propertyDialogFragment")
    }

    fun openDateDialogFragment(editDate: EditText, previousDate: String?, fragmentManager: FragmentManager) {
        val dateDialogFragment = DateDialogFragment(editDate, previousDate)
        dateDialogFragment.show(fragmentManager, "dateDialogFragment")
    }

}
