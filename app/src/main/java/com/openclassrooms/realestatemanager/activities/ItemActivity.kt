package com.openclassrooms.realestatemanager.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Type


class ItemActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    companion object {
        // Key for item type
        const val TYPE_ITEM = "typeItem"
        const val PICTURE_ITEM = "pictureItem"
    }

    private lateinit var textType: String
    private lateinit var pictureText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        val spinnerType: Spinner = findViewById(R.id.activity_item_type)
        val editText: EditText = findViewById(R.id.activity_item_picture)

        // Retrieve the path of a picture in the editText
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                pictureText = editText.text.toString()
            }
        })

        // Retrieve the list of types of an Item with the Type Enum
        val typeList = Type.values()
        // Create adapter and add in spinnerType
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, typeList)
        spinnerType.adapter = spinnerAdapter
        spinnerType.onItemSelectedListener = this

        saveCreatedItem()
    }

    //----------------------------------------------------------------------------------
    // Methods for spinnerType

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        textType = parent?.getItemAtPosition(position).toString()
        Toast.makeText(parent?.context, textType, Toast.LENGTH_SHORT).show()
    }

    //----------------------------------------------------------------------------------
    // Private methods

    private fun saveCreatedItem() {
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
