package com.openclassrooms.realestatemanager.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Status

class StatusDialogFragment(private var editStatus: EditText) : DialogFragment() {

    // Declare callback
    private var callbackStatus: OnStatusChosenListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            var status: String?

            // Create a charSequence array of the Status Enum
            val statutes: Array<CharSequence> =
                    arrayOf(Status.AVAILABLE.disponibility, Status.SOLD.disponibility)

            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.real_estate_status)
                    .setItems(statutes) { _, which ->
                        // The 'which' argument contains the index position of the selected item
                        // Set and save the selected value for the status item
                        status = statutes[which] as String
                        callbackStatus?.onStatusChosen(status)
                        editStatus.setText(status)
                    }
                    // Set the negative action button
                    .setNegativeButton(getString(R.string.erase)) { _, _ ->
                        // Set and save null value for the status item
                        status = null
                        callbackStatus?.onStatusChosen(status)
                        // Set hint text by default
                        editStatus.setText(activity?.getString(R.string.real_estate_status))
                    }
            // Create the AlertDialog object and return it
            builder.create()
            builder.show()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    //----------------------------------------------------------------------------------
    // Interface for callback to parent activity and associated methods when choose a status item

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the method that creating callback after being attached to parent activity
        createCallbackToParentActivity()
    }

    // Declare our interface that will be implemented by any container activity
    interface OnStatusChosenListener {
        fun onStatusChosen(statusChosen: String?)
    }

    // Create callback to parent activity
    private fun createCallbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            callbackStatus = activity as OnStatusChosenListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement OnStatusChosenListener")
        }
    }

}