package com.openclassrooms.realestatemanager.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Type

class TypeDialogFragment(private var editType: EditText) : DialogFragment() {

    // Declare callback
    private var callbackType: OnTypeChosenListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            var type: String?

            // Create a charSequence array of the Type Enum
            val types: Array<CharSequence> = arrayOf(Type.DUPLEX.itemType, Type.FLAT.itemType,
                    Type.LOFT.itemType, Type.MANOR.itemType, Type.PENTHOUSE.itemType)

            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.real_estate_type)
                    .setItems(types) { _, which ->
                        // The 'which' argument contains the index position of the selected item
                        // Set and save the selected value for the type item
                        type = types[which] as String
                        callbackType?.onTypeChosen(type)
                        editType.setText(type)
                    }
                    // Set the negative action button
                    .setNegativeButton(getString(R.string.erase)) { _, _ ->
                        // Set and save null value for the type item
                        type = null
                        callbackType?.onTypeChosen(type)
                        // Set text by default
                        editType.setText(activity?.getString(R.string.real_estate_type))
                    }
            // Create the AlertDialog object and return it
            builder.create()
            builder.show()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    //----------------------------------------------------------------------------------
    // Interface for callback to parent activity and associated methods when choose a type item

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the method that creating callback after being attached to parent activity
        createCallbackToParentActivity()
    }

    // Declare our interface that will be implemented by any container activity
    interface OnTypeChosenListener {
        fun onTypeChosen(typeChosen: String?)
    }

    // Create callback to parent activity
    private fun createCallbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            callbackType = activity as OnTypeChosenListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement OnTypeChosenListener")
        }
    }

}