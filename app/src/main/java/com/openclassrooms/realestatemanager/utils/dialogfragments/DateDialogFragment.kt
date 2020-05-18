package com.openclassrooms.realestatemanager.utils.dialogfragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.openclassrooms.realestatemanager.R
import java.text.SimpleDateFormat
import java.util.*


class DateDialogFragment(private var editDate: EditText)
    : DialogFragment(), DatePickerDialog.OnDateSetListener {

    // Declare callback
    private var callbackDate: OnDateListener? = null

    private val calendar: Calendar = Calendar.getInstance()
    private val today: Date = calendar.time
    private lateinit var dateChosen: Date

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Set neutral button to erase data
        val dialog = activity?.let { DatePickerDialog(it, 0, this, year, month, day) }
        dialog?.datePicker?.minDate
        dialog?.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.erase)) { _, _ ->
            editDate.text.clear()
            callbackDate?.onDateChosen(editDate)
        }

        return dialog!!
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Set the date chosen
        calendar.set(year, month, day)
        dateChosen = calendar.time

        // The date chosen cannot be later than today
        if (dateChosen.after(today)) {
            Toast.makeText(activity, getString(R.string.impossible_future_date), Toast.LENGTH_LONG).show()
        } else {
            // Display the date chosen in the editText
            displayDate(editDate)
            // Callback to parent activity
            callbackDate?.onDateChosen(editDate)
        }
    }

    //---------------------------------------------------------------
    // Method to display and format the date in the editText
    private fun displayDate(editText: EditText) {
        // Set date format
        val dateFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        // Update the editText with the chosen date
        editText.setText(sdf.format(dateChosen))
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

    override fun onDetach() {
        super.onDetach()
        callbackDate = null
    }

}