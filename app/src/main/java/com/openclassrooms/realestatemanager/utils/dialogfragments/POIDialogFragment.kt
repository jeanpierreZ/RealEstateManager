package com.openclassrooms.realestatemanager.utils.dialogfragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.PointsOfInterest

class POIDialogFragment(private var editPOI: EditText,
                        private val previouslySelectedItems: ArrayList<String>?) : DialogFragment() {

    // Declare callback
    private var callbackPOI: OnPOIChosenListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            // Points of interest chosen to attach to the callback
            val pointsOfInterestChosen: ArrayList<String>? = arrayListOf()

            // Create a charSequence array of the PointsOfInterest Enum
            val pointsOfInterest: Array<CharSequence> = arrayOf(
                    PointsOfInterest.PARK.type,
                    PointsOfInterest.RESTAURANT.type,
                    PointsOfInterest.SCHOOL.type,
                    PointsOfInterest.SHOP.type,
                    PointsOfInterest.TRAIN_STATION.type)

            // Where we track the selected items in MultiChoiceItems
            val selectedItems = ArrayList<Int>()

            // List of preselected items to pass for dialog UI
            val preSelectedItems = booleanArrayOf(false, false, false, false, false)

            // Check true if an item was previously selected for UI and add it to the selected items
            if (!previouslySelectedItems.isNullOrEmpty()) {
                for (item in previouslySelectedItems) {
                    when (item) {
                        PointsOfInterest.PARK.type -> {
                            preSelectedItems[0] = true; selectedItems.add(0)
                        }
                        PointsOfInterest.RESTAURANT.type -> {
                            preSelectedItems[1] = true; selectedItems.add(1)
                        }
                        PointsOfInterest.SCHOOL.type -> {
                            preSelectedItems[2] = true; selectedItems.add(2)
                        }
                        PointsOfInterest.SHOP.type -> {
                            preSelectedItems[3] = true; selectedItems.add(3)
                        }
                        PointsOfInterest.TRAIN_STATION.type -> {
                            preSelectedItems[4] = true; selectedItems.add(4)
                        }
                    }
                }
            }

            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it, R.style.DialogTheme)
            builder.setTitle(R.string.real_estate_points_of_interest)
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setMultiChoiceItems(pointsOfInterest, preSelectedItems)
                    { _, which, isChecked ->
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
                    { _, _ ->
                        // User clicked OK, so save the selectedItems results
                        // For each selected item, add the of point of interest in the list
                        for (item in selectedItems) {
                            when (item) {
                                0 -> pointsOfInterestChosen?.add(PointsOfInterest.PARK.type)
                                1 -> pointsOfInterestChosen?.add(PointsOfInterest.RESTAURANT.type)
                                2 -> pointsOfInterestChosen?.add(PointsOfInterest.SCHOOL.type)
                                3 -> pointsOfInterestChosen?.add(PointsOfInterest.SHOP.type)
                                4 -> pointsOfInterestChosen?.add(PointsOfInterest.TRAIN_STATION.type)
                            }
                        }
                        // Save the selected values for the points of interest of the item
                        callbackPOI?.onPOIChosen(pointsOfInterestChosen)
                        // Display the chosen points of interest
                        val displayPOI = pointsOfInterestChosen?.joinToString { it -> it }
                        editPOI.setText(displayPOI)
                    }
                    .setNegativeButton(android.R.string.cancel)
                    { dialog, _ ->
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

    override fun onDetach() {
        super.onDetach()
        callbackPOI = null
    }
}
