package com.openclassrooms.realestatemanager.activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Type


class ItemActivity : AppCompatActivity() {

    companion object {

        // Key for item type
        const val TYPE_ITEM = "TYPE_ITEM"
        const val PRICE_ITEM = "PRICE_ITEM"
        const val PICTURE_ITEM = "pictureItem"
    }

    private var type: String? = null
    private var price: Int? = null
    private lateinit var pictureText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        val editType: EditText = findViewById(R.id.activity_item_edit_type)
        val editPrice: EditText = findViewById(R.id.activity_item_edit_price)
        val picture: EditText = findViewById(R.id.activity_item_picture)

        /*val typeListDialogFragment = TypeListDialogFragment(textType, typeView)

        typeListDialogFragment.onCreateDialog(savedInstanceState).show()*/

        editType.setOnClickListener {
            // Create a charSequence array of Type Enum
            val types: Array<CharSequence> = arrayOf(Type.DUPLEX.itemType, Type.FLAT.itemType,
                    Type.LOFT.itemType, Type.MANOR.itemType, Type.PENTHOUSE.itemType)

            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(this)
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
        }

        editPrice.doOnTextChanged { text, start, count, after ->
            price = text.toString().toIntOrNull()
        }

        // Retrieve the path of a picture in the editText
        picture.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        pictureText = picture.text.toString()
                    }
                })

        saveItem()
    }

    //----------------------------------------------------------------------------------
    // Private methods

    private fun saveItem() {
        val saveButton = findViewById<Button>(R.id.activity_item_button_save)
        saveButton?.setOnClickListener {
            val replyIntent = Intent()
            replyIntent.putExtra(TYPE_ITEM, type)
            replyIntent.putExtra(PRICE_ITEM, price)
            replyIntent.putExtra(PICTURE_ITEM, pictureText)
            setResult(Activity.RESULT_OK, replyIntent)
            finish()
        }
    }

}
