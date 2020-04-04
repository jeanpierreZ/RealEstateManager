package com.openclassrooms.realestatemanager.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.openclassrooms.realestatemanager.R

class PropertyDialogFragment(private var editText: EditText,
                             private val title: Int,
                             private val list: Array<CharSequence>) : DialogFragment() {

    // Declare callback
    private var callbackProperty: OnPropertyChosenListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            var choice: String?

            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setTitle(title)
                    .setItems(list) { _, which ->
                        // The 'which' argument contains the index position of the selected item
                        // Set and save the selected value for the property of the item
                        choice = list[which] as String
                        editText.setText(choice)
                        callbackProperty?.onPropertyChosen(editText)
                    }
                    // Set the negative action button
                    .setNegativeButton(getString(R.string.erase)) { _, _ ->
                        // Save a null value for the property of the item
                        callbackProperty?.onPropertyChosen(null)
                        // Set hint text by default
                        editText.setText(getString(title))
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