package com.openclassrooms.realestatemanager.utils

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Type

class TypeListDialogFragment(private var textType: String?, private var typeView: TextView)
    : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            // Create a charSequence array of Type Enum
            val types: Array<CharSequence> = arrayOf(Type.DUPLEX.itemType, Type.FLAT.itemType,
                    Type.LOFT.itemType, Type.MANOR.itemType, Type.PENTHOUSE.itemType)

            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.realEstateType)
                    .setItems(types,
                            DialogInterface.OnClickListener { dialog, which ->
                                // The 'which' argument contains the index position of the selected item
                                textType = types[which] as String
                                typeView.text = textType
                            })
                    // Set the action buttons
                    .setNegativeButton("Erase",
                            DialogInterface.OnClickListener { dialog, id ->
                                textType = ""
                                typeView.text = getString(R.string.realEstateType)
                            })
            // Create the AlertDialog object and return it
            builder.create()
            builder.show()

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}