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
import com.openclassrooms.realestatemanager.utils.POIDialogFragment
import com.openclassrooms.realestatemanager.utils.TypeDialogFragment


class ItemActivity : AppCompatActivity(),
        TypeDialogFragment.OnTypeChosenListener,
        POIDialogFragment.OnPOIChosenListener{

    companion object {
        // Keys for item attributes
        const val TYPE_ITEM = "TYPE_ITEM"
        const val PRICE_ITEM = "PRICE_ITEM"
        const val SURFACE_ITEM = "SURFACE_ITEM"
        const val ROOMS_ITEM = "ROOMS_ITEM"
        const val BATHROOMS_ITEM = "BATHROOMS_ITEM"
        const val BEDROOMS_ITEM = "BEDROOMS_ITEM"
        const val POI_ITEM = "POI_ITEM"

        const val PICTURE_ITEM = "PICTURE_ITEM"
    }

    // Properties of a real estate
    private var type: String? = null
    private var price: Int? = null
    private var surface: Int? = null
    private var rooms: Int? = null
    private var bathrooms: Int? = null
    private var bedrooms: Int? = null
    private var pointsOfInterest: ArrayList<String>? = null

    private lateinit var editType: EditText
    private lateinit var editPOI: EditText

    private var pictureText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        editType = findViewById(R.id.activity_item_edit_type)
        val editPrice: EditText = findViewById(R.id.activity_item_edit_price)
        val editSurface: EditText = findViewById(R.id.activity_item_edit_surface)
        val editRooms: EditText = findViewById(R.id.activity_item_edit_rooms)
        val editBathrooms: EditText = findViewById(R.id.activity_item_edit_bathrooms)
        val editBedrooms: EditText = findViewById(R.id.activity_item_edit_bedrooms)
        editPOI = findViewById(R.id.activity_item_edit_poi)

        val picture: EditText = findViewById(R.id.activity_item_picture)

        //----------------------------------------------------------------------------------
        // Get the texts typed in the editTexts

        // Show the AlertDialog to choose the type of the real estate
        editType.setOnClickListener {
            openTypeDialogFragment()
        }

        editPrice.doOnTextChanged { text, start, count, after ->
            price = text.toString().toIntOrNull()
        }

        editSurface.doOnTextChanged { text, start, count, after ->
            surface = text.toString().toIntOrNull()
        }

        editRooms.doOnTextChanged { text, start, count, after ->
            rooms = text.toString().toIntOrNull()
        }

        editBathrooms.doOnTextChanged { text, start, count, after ->
            bathrooms = text.toString().toIntOrNull()
        }

        editBedrooms.doOnTextChanged { text, start, count, after ->
            bedrooms = text.toString().toIntOrNull()
        }

        // Show the AlertDialog to choose the points of interest of the real estate
        editPOI.setOnClickListener {
            openPOIDialogFragment()
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


    private fun openPOIDialogFragment() {
        val pOIDialogFragment = POIDialogFragment(pointsOfInterest, editPOI)
        pOIDialogFragment.show(supportFragmentManager, "pOIDialogFragment")
    }

    private fun saveItem() {
        val saveButton = findViewById<Button>(R.id.activity_item_button_save)
        saveButton?.setOnClickListener {
            val replyIntent = Intent()
            replyIntent.putExtra(TYPE_ITEM, type)
            replyIntent.putExtra(PRICE_ITEM, price)
            replyIntent.putExtra(SURFACE_ITEM, surface)
            replyIntent.putExtra(ROOMS_ITEM, rooms)
            replyIntent.putExtra(BATHROOMS_ITEM, bathrooms)
            replyIntent.putExtra(BEDROOMS_ITEM, bedrooms)
            replyIntent.putExtra(POI_ITEM, pointsOfInterest)

            replyIntent.putExtra(PICTURE_ITEM, pictureText)
            setResult(Activity.RESULT_OK, replyIntent)
            finish()
        }
    }

    //----------------------------------------------------------------------------------
    // Implement listener from the DialogFragments to fetch the data of the real estate

    override fun onTypeChosen(typeChosen: String?) {
        type = typeChosen
    }

    override fun onPOIChosen(POIChosen: ArrayList<String>?) {
        pointsOfInterest = POIChosen
    }
}
