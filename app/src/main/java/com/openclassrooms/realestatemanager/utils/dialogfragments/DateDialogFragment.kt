package com.openclassrooms.realestatemanager.utils.dialogfragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utils.MyUtils
import java.text.SimpleDateFormat
import java.util.*


class DateDialogFragment(private var editDate: EditText, private val previousDate: String?)
    : DialogFragment(), DatePickerDialog.OnDateSetListener {

    // Declare callback
    private var callbackDate: OnDateListener? = null

    // Set the calendar with today's date
    private val calendar = Calendar.getInstance()
    private val today = calendar.time

    // The date chosen by the user
    private lateinit var dateChosen: Date

    // Set date format
    private val dateFormat = "MM/dd/yyyy"
    private val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())

    // Get the date from the calendar
    private var year = calendar.get(Calendar.YEAR)
    private var month = calendar.get(Calendar.MONTH)
    private var day = calendar.get(Calendar.DAY_OF_MONTH)

    private val myUtils = MyUtils()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // If there is a previous choice, set the calendar with the date
        if (previousDate != null) {
            calendar.time = sdf.parse(previousDate)!!
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)
        }

        // Set DatePickerDialog with date from the calendar
        val dialog = activity?.let { DatePickerDialog(it, R.style.DialogButtonTheme, this, year, month, day) }
        dialog?.datePicker?.minDate

        // Set neutral button to erase data
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
            myUtils.showSnackbarMessage(requireActivity(), R.string.impossible_future_date)
        } else {
            // Display the date chosen in the editText
            displayDate()
            // Callback to parent activity
            callbackDate?.onDateChosen(editDate)
        }
    }

    //---------------------------------------------------------------
    // Method to display and format the date in the editText
    private fun displayDate() {
        // Update the editText with the chosen date
        editDate.setText(sdf.format(dateChosen))
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
