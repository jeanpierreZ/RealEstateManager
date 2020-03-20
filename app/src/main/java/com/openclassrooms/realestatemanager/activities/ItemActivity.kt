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
import com.openclassrooms.realestatemanager.utils.DateDialogFragment
import com.openclassrooms.realestatemanager.utils.POIDialogFragment
import com.openclassrooms.realestatemanager.utils.StatusDialogFragment
import com.openclassrooms.realestatemanager.utils.TypeDialogFragment


class ItemActivity : AppCompatActivity(),
        TypeDialogFragment.OnTypeChosenListener, POIDialogFragment.OnPOIChosenListener,
        StatusDialogFragment.OnStatusChosenListener, DateDialogFragment.OnDateListener {

    companion object {
        // Keys for item attributes
        const val TYPE_ITEM = "TYPE_ITEM"
        const val PRICE_ITEM = "PRICE_ITEM"
        const val SURFACE_ITEM = "SURFACE_ITEM"
        const val ROOMS_ITEM = "ROOMS_ITEM"
        const val BATHROOMS_ITEM = "BATHROOMS_ITEM"
        const val BEDROOMS_ITEM = "BEDROOMS_ITEM"
        const val POI_ITEM = "POI_ITEM"
        const val STREET_NUMBER_ITEM = "STREET_NUMBER_ITEM"
        const val STREET_ITEM = "STREET_ITEM"
        const val APARTMENT_NUMBER_ITEM = "APARTMENT_NUMBER_ITEM"
        const val DISTRICT_ITEM = "DISTRICT_ITEM"
        const val CITY_ITEM = "CITY_ITEM"
        const val POSTAL_CODE_ITEM = "POSTAL_CODE_ITEM"
        const val COUNTRY_ITEM = "COUNTRY_ITEM"
        const val DESCRIPTION_ITEM = "DESCRIPTION_ITEM"
        const val STATUS_ITEM = "STATUS_ITEM"
        const val ENTRY_DATE_ITEM = "ENTRY_DATE_ITEM"
        const val SALE_DATE_ITEM = "SALE_DATE_ITEM"
        const val AGENT_ITEM = "AGENT_ITEM"

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
    private var streetNumber: String? = null
    private var street: String? = null
    private var apartmentNumber: String? = null
    private var district: String? = null
    private var city: String? = null
    private var postalCode: String? = null
    private var country: String? = null
    private var description: String? = null
    private var status: String? = null
    private var entryDate: String? = null
    private var saleDate: String? = null
    private var agent: String? = null

    private lateinit var editType: EditText
    private lateinit var editPOI: EditText
    private lateinit var editStatus: EditText
    private lateinit var editEntryDate: EditText
    private lateinit var editSaleDate: EditText

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
        val editStreetNumber: EditText = findViewById(R.id.activity_item_edit_street_number)
        val editStreet: EditText = findViewById(R.id.activity_item_edit_street)
        val editApartmentNumber: EditText = findViewById(R.id.activity_item_edit_apartment_number)
        val editDistrict: EditText = findViewById(R.id.activity_item_edit_district)
        val editCity: EditText = findViewById(R.id.activity_item_edit_city)
        val editPostalCode: EditText = findViewById(R.id.activity_item_edit_postal_code)
        val editCountry: EditText = findViewById(R.id.activity_item_edit_country)
        val editDescription: EditText = findViewById(R.id.activity_item_edit_description)
        editStatus = findViewById(R.id.activity_item_edit_status)
        editEntryDate = findViewById(R.id.activity_item_edit_entry_date)
        editSaleDate = findViewById(R.id.activity_item_edit_sale_date)
        val editAgent: EditText = findViewById(R.id.activity_item_edit_agent)

        val picture: EditText = findViewById(R.id.activity_item_picture)

        //----------------------------------------------------------------------------------
        // Get the texts typed in the editTexts

        // Show the AlertDialog to choose the type of the real estate
        editType.setOnClickListener {
            openTypeDialogFragment()
        }

        editPrice.doOnTextChanged { text, _, _, _ ->
            price = text.toString().toIntOrNull()
        }

        editSurface.doOnTextChanged { text, _, _, _ ->
            surface = text.toString().toIntOrNull()
        }

        editRooms.doOnTextChanged { text, _, _, _ ->
            rooms = text.toString().toIntOrNull()
        }

        editBathrooms.doOnTextChanged { text, _, _, _ ->
            bathrooms = text.toString().toIntOrNull()
        }

        editBedrooms.doOnTextChanged { text, _, _, _ ->
            bedrooms = text.toString().toIntOrNull()
        }

        // Show the AlertDialog to choose the points of interest of the real estate
        editPOI.setOnClickListener {
            openPOIDialogFragment()
        }

        editStreetNumber.doOnTextChanged { text, _, _, _ ->
            streetNumber = text.toString()
        }

        editStreet.doOnTextChanged { text, _, _, _ ->
            street = text.toString()
        }

        editApartmentNumber.doOnTextChanged { text, _, _, _ ->
            apartmentNumber = text.toString()
        }

        editDistrict.doOnTextChanged { text, _, _, _ ->
            district = text.toString()
        }

        editCity.doOnTextChanged { text, _, _, _ ->
            city = text.toString()
        }

        editPostalCode.doOnTextChanged { text, _, _, _ ->
            postalCode = text.toString()
        }

        editCountry.doOnTextChanged { text, _, _, _ ->
            country = text.toString()
        }

        editDescription.doOnTextChanged { text, _, _, _ ->
            description = text.toString()
        }

        // Show the AlertDialog to choose the status of the real estate
        editStatus.setOnClickListener {
            openStatusDialogFragment()
        }

        // Show the AlertDialog to choose the entry date of the real estate
        editEntryDate.setOnClickListener {
            openDateDialogFragment(editEntryDate)
        }

        // Show the AlertDialog to choose the sale date of the real estate
        editSaleDate.setOnClickListener {
            openDateDialogFragment(editSaleDate)
        }

        editAgent.doOnTextChanged { text, _, _, _ ->
            agent = text.toString()
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
        val typeDialogFragment = TypeDialogFragment(editType)
        typeDialogFragment.show(supportFragmentManager, "typeDialogFragment")
    }

    private fun openPOIDialogFragment() {
        val pOIDialogFragment = POIDialogFragment(editPOI)
        pOIDialogFragment.show(supportFragmentManager, "pOIDialogFragment")
    }

    private fun openStatusDialogFragment() {
        val statusDialogFragment = StatusDialogFragment(editStatus)
        statusDialogFragment.show(supportFragmentManager, "statusDialogFragment")
    }

    private fun openDateDialogFragment(editDate: EditText) {
        val dateDialogFragment = DateDialogFragment(editDate)
        dateDialogFragment.show(supportFragmentManager, "dateDialogFragment")
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
            replyIntent.putExtra(STREET_NUMBER_ITEM, streetNumber)
            replyIntent.putExtra(STREET_ITEM, street)
            replyIntent.putExtra(APARTMENT_NUMBER_ITEM, apartmentNumber)
            replyIntent.putExtra(DISTRICT_ITEM, district)
            replyIntent.putExtra(CITY_ITEM, city)
            replyIntent.putExtra(POSTAL_CODE_ITEM, postalCode)
            replyIntent.putExtra(COUNTRY_ITEM, country)
            replyIntent.putExtra(DESCRIPTION_ITEM, description)
            replyIntent.putExtra(STATUS_ITEM, status)
            replyIntent.putExtra(ENTRY_DATE_ITEM, entryDate)
            replyIntent.putExtra(SALE_DATE_ITEM, saleDate)
            replyIntent.putExtra(AGENT_ITEM, agent)

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

    override fun onStatusChosen(statusChosen: String?) {
        status = statusChosen
    }

    override fun onDateChosen(editTextChosen: EditText?) {
        when (editTextChosen) {
            editEntryDate -> entryDate = editTextChosen.text.toString()
            editSaleDate -> saleDate = editTextChosen.text.toString()
        }
    }

}
