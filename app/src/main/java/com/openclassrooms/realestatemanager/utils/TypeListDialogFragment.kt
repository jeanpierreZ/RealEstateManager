package com.openclassrooms.realestatemanager.utils

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Type

class TypeListDialogFragment(private var type: String?, private var editType: EditText)
    : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            // Create a charSequence array of Type Enum
            val types: Array<CharSequence> = arrayOf(Type.DUPLEX.itemType, Type.FLAT.itemType,
                    Type.LOFT.itemType, Type.MANOR.itemType, Type.PENTHOUSE.itemType)

            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.real_estate_type)
                    .setItems(types,
                            DialogInterface.OnClickListener { dialog, which ->
                                // The 'which' argument contains the index position of the selected item
                                type = types[which] as String
                                editType.setText(type)
                            })
                    // Set the action buttons
                    .setNegativeButton(getString(R.string.erase),
                            DialogInterface.OnClickListener { dialog, id ->
                                type = ""
                                editType.setText(getString(R.string.real_estate_type))
                            })
            // Create the AlertDialog object and return it
            builder.create()
            builder.show()

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}