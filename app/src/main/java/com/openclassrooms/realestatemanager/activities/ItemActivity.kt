package com.openclassrooms.realestatemanager.activities

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Type


class ItemActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    companion object {
        // Key for item type
        const val TYPE_ITEM = "typeItem"
    }

    private lateinit var textType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        val spinnerType: Spinner = findViewById(R.id.activity_item_type)

        // Retrieve the list of types of an Item with the Type Enum
        val typeList = Type.values()
        // Create adapter and add in spinnerType
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, typeList)
        spinnerType.adapter = spinnerAdapter
        spinnerType.onItemSelectedListener = this
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
}
