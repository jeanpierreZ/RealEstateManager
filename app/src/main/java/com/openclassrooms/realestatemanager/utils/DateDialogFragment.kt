package com.openclassrooms.realestatemanager.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*

class DateDialogFragment(private var editDate: EditText)
    : DialogFragment(), DatePickerDialog.OnDateSetListener {

    // Declare callback
    private var callbackDate: OnDateListener? = null

    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Use the current date as the default date in the picker
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Save the date chosen by the user
        calendar.set(year, month, day)

        // Display and set the date chosen with calendar
        displayDate(editDate)

        // Callback to parent activity
        callbackDate?.onDateChosen(editDate)
    }

    //---------------------------------------------------------------
    // Method to display and format the date in the editText
    private fun displayDate(editText: EditText) {
        // Set date format
        val dateFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        // Update the editText with the chosen date
        editText.setText(sdf.format(calendar.time))
    }

    //----------------------------------------------------------------------------------
    // Interface for callback to parent activity and associated methods when choose a date

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the method that creating callback after being attached to parent activity
        createCallbackToParentActivity()
    }

    // Declare our interface that will be implemented by any container activity
    interface OnDateListener {
        fun onDateChosen(editTextChosen: EditText?)
    }

    // Create callback to parent activity
    private fun createCallbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            callbackDate = activity as OnDateListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement OnDateListener")
        }
    }

}