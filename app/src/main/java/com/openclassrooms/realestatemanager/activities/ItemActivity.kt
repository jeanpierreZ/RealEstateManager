package com.openclassrooms.realestatemanager.activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Type
import com.openclassrooms.realestatemanager.utils.TypeListDialogFragment


class ItemActivity : AppCompatActivity() {

    companion object {
        // Key for item type
        const val TYPE_ITEM = "typeItem"
        const val PICTURE_ITEM = "pictureItem"
    }

    private var textType: String? = null
    private lateinit var pictureText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        val typeView: TextView = findViewById(R.id.activity_item_type)
        val editText: EditText = findViewById(R.id.activity_item_picture)

        /*val typeListDialogFragment = TypeListDialogFragment(textType, typeView)

        typeListDialogFragment.onCreateDialog(savedInstanceState).show()*/

        typeView.setOnClickListener {
            // Create a charSequence array of Type Enum
            val types: Array<CharSequence> = arrayOf(Type.DUPLEX.itemType, Type.FLAT.itemType,
                    Type.LOFT.itemType, Type.MANOR.itemType, Type.PENTHOUSE.itemType)

            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(this)
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
        }

        // Retrieve the path of a picture in the editText
        editText.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        pictureText = editText.text.toString()
                    }
                })

        saveItem()
    }

    //----------------------------------------------------------------------------------
    // Private methods

    private fun saveItem() {
        val saveButton = findViewById<Button>(R.id.button_save)
        saveButton?.setOnClickListener {
            val replyIntent = Intent()
            replyIntent.putExtra(TYPE_ITEM, textType)
            replyIntent.putExtra(PICTURE_ITEM, pictureText)
            setResult(Activity.RESULT_OK, replyIntent)
            finish()
        }
    }

}
