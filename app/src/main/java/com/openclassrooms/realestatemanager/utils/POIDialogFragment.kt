package com.openclassrooms.realestatemanager.utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.PointsOfInterest

class POIDialogFragment(private var pointsOfInterestList: ArrayList<String>?, private var editPOI: EditText)
    : DialogFragment() {

    // Declare callback
    private var callbackPOI: OnPOIChosenListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            // Create a charSequence array of the PointsOfInterest Enum
            val pointsOfInterest: Array<CharSequence> = arrayOf(
                    PointsOfInterest.PARK.type,
                    PointsOfInterest.RESTAURANT.type,
                    PointsOfInterest.SCHOOL.type,
                    PointsOfInterest.SHOP.type,
                    PointsOfInterest.TRAIN_STATION.type)

            // Where we track the selected items in MultiChoiceItems
            val selectedItems = ArrayList<Int>()

            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.real_estate_points_of_interest)
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setMultiChoiceItems(pointsOfInterest, null)
                    { dialog, which, isChecked ->
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            selectedItems.add(which)
                        } else if (selectedItems.contains(which)) {
                            // Else, if the item is already in the array, remove it
                            selectedItems.remove(Integer.valueOf(which))
                        }
                    }
                    // Set the action buttons
                    .setPositiveButton(android.R.string.ok)
                    { dialog, id ->
                        // User clicked OK, so save the selectedItems results
                        // Instantiate pointsOfInterestList
                        pointsOfInterestList = arrayListOf()
                        // For each selected item, add the of point of interest in the list
                        for (item in selectedItems) {
                            when (item) {
                                0 -> pointsOfInterestList?.add(PointsOfInterest.PARK.type)
                                1 -> pointsOfInterestList?.add(PointsOfInterest.RESTAURANT.type)
                                2 -> pointsOfInterestList?.add(PointsOfInterest.SCHOOL.type)
                                3 -> pointsOfInterestList?.add(PointsOfInterest.SHOP.type)
                                4 -> pointsOfInterestList?.add(PointsOfInterest.TRAIN_STATION.type)
                            }
                        }
                        // Save the selected values for the points of interest of the item
                        callbackPOI?.onPOIChosen(pointsOfInterestList)
                        // Display the chosen points of interest
                        val displayPOI = pointsOfInterestList?.joinToString { it -> it }
                        editPOI.setText(displayPOI)
                    }
                    .setNegativeButton(android.R.string.cancel)
                    { dialog, id ->
                        dialog.dismiss()
                    }
            // Create the AlertDialog object and return it
            builder.create()
            builder.show()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    //----------------------------------------------------------------------------------
    // Interface for callback to parent activity and associated methods
    // when choose the points of interest of the item

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the method that creating callback after being attached to parent activity
        createCallbackToParentActivity()
    }

    // Declare our interface that will be implemented by any container activity
    interface OnPOIChosenListener {
        fun onPOIChosen(POIChosen: ArrayList<String>?)
    }

    // Create callback to parent activity
    private fun createCallbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            callbackPOI = activity as OnPOIChosenListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement OnPOIChosenListener")
        }
    }
}