package com.openclassrooms.realestatemanager.utils.dialogfragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Status
import com.openclassrooms.realestatemanager.models.Type

class PropertyDialogFragment(private var editText: EditText,
                             private val title: Int,
                             private val previouslySelectedChoice: String?,
                             private val list: Array<CharSequence>) : DialogFragment() {

    // Declare callback
    private var callbackProperty: OnPropertyChosenListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            // Preselected item to pass for dialog in SingleChoiceItems
            var preSelectedChoice: Int = -1

            // Update value if the item was previously selected
            if (!previouslySelectedChoice.isNullOrEmpty()) {
                if (list.contains(Type.DUPLEX.realEstateType)) {
                    when (previouslySelectedChoice) {
                        Type.DUPLEX.realEstateType -> preSelectedChoice = 0
                        Type.FLAT.realEstateType -> preSelectedChoice = 1
                        Type.LOFT.realEstateType -> preSelectedChoice = 2
                        Type.MANOR.realEstateType -> preSelectedChoice = 3
                        Type.PENTHOUSE.realEstateType -> preSelectedChoice = 4
                    }
                } else {
                    when (previouslySelectedChoice) {
                        Status.AVAILABLE.availability -> preSelectedChoice = 0
                        Status.SOLD.availability -> preSelectedChoice = 1
                    }
                }
            }

            var choice: String? = null

            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setTitle(title)
                    // Specify the list array, the item to be selected by default (-1 for none),
                    // and the listener through which to receive callbacks which item is selected
                    .setSingleChoiceItems(list, preSelectedChoice) { _, which ->
                        // The 'which' argument contains the index position of the selected item
                        choice = list[which] as String
                    }
                    .setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                        // Set and save the selected value for the property or the status of the item
                        editText.setText(choice)
                        callbackProperty?.onPropertyChosen(editText)
                    }
                    // Set the negative action button
                    .setNegativeButton(getString(R.string.erase)) { _, _ ->
                        // Save a null value for the property of the item
                        editText.text = null
                        callbackProperty?.onPropertyChosen(editText)
                    }
            // Create the AlertDialog object and return it
            builder.create()
            builder.show()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    //----------------------------------------------------------------------------------
    // Interface for callback to parent activity and associated methods when choose a property of an item

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the method that creating callback after being attached to parent activity
        createCallbackToParentActivity()
    }

    // Declare our interface that will be implemented by any container activity
    interface OnPropertyChosenListener {
        fun onPropertyChosen(propertyChosen: EditText?)
    }

    // Create callback to parent activity
    private fun createCallbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            callbackProperty = activity as OnPropertyChosenListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement OnPropertyChosenListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbackProperty = null
    }
}