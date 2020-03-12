package com.openclassrooms.realestatemanager.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utils.TypeDialogFragment


class ItemActivity : AppCompatActivity(), TypeDialogFragment.OnTypeChosenListener {

    companion object {
        // Keys for item attributes
        const val TYPE_ITEM = "TYPE_ITEM"
        const val PRICE_ITEM = "PRICE_ITEM"
        const val PICTURE_ITEM = "pictureItem"
    }

    private var type: String? = null
    private var price: Int? = null
    private lateinit var pictureText: String
    private lateinit var editType: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        editType = findViewById(R.id.activity_item_edit_type)
        val editPrice: EditText = findViewById(R.id.activity_item_edit_price)
        val picture: EditText = findViewById(R.id.activity_item_picture)

        // Show the AlertDialog to choose the type of the real estate
        editType.setOnClickListener {
            openTypeDialogFragment()    
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

    private fun openTypeDialogFragment() {
        val typeDialogFragment = TypeDialogFragment(type, editType)
        typeDialogFragment.show(supportFragmentManager, "typeDialogFragment")
    }

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

    //----------------------------------------------------------------------------------
    // Implement listener from TypeDialogFragment to fetch the type of the real estate

    override fun onTypeChosen(typeChosen: String?) {
        type = typeChosen
    }

}
