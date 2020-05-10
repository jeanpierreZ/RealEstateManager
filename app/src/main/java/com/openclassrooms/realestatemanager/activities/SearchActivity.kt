package com.openclassrooms.realestatemanager.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Type
import com.openclassrooms.realestatemanager.utils.DateDialogFragment
import com.openclassrooms.realestatemanager.utils.MyUtils
import com.openclassrooms.realestatemanager.utils.POIDialogFragment
import com.openclassrooms.realestatemanager.utils.PropertyDialogFragment
import kotlinx.android.synthetic.main.activity_search.*
import java.text.SimpleDateFormat
import java.util.*

class SearchActivity : AppCompatActivity(),
        PropertyDialogFragment.OnPropertyChosenListener,
        POIDialogFragment.OnPOIChosenListener,
        DateDialogFragment.OnDateListener {

    companion object {
        private val TAG = SearchActivity::class.java.simpleName
    }

    private val myUtils = MyUtils()

    // Create a charSequence array of the Type Enum
    private val types: Array<CharSequence> =
            arrayOf(Type.DUPLEX.itemType, Type.FLAT.itemType, Type.LOFT.itemType,
                    Type.MANOR.itemType, Type.PENTHOUSE.itemType)

    private var type: String? = null
    private var minPrice: Int? = null
    private var maxPrice: Int? = null
    private var minSurface: Int? = null
    private var maxSurface: Int? = null
    private var pointsOfInterest: ArrayList<String>? = null
    private var district: String? = null
    private var entryDate: String? = null
    private var saleDate: String? = null
    private var pictureNumber: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        configureToolbar()

        //----------------------------------------------------------------------------------
        // Get data

        // Show the AlertDialog to choose the type of the real estate
        activity_search_edit_type.setOnClickListener {
            myUtils.openPropertyDialogFragment(activity_search_edit_type, R.string.real_estate_type, types, supportFragmentManager)
        }

        activity_search_edit_price_min.doOnTextChanged { text, _, _, _ ->
            minPrice = text.toString().toIntOrNull()
        }

        activity_search_edit_price_max.doOnTextChanged { text, _, _, _ ->
            maxPrice = text.toString().toIntOrNull()
        }

        activity_search_edit_surface_min.doOnTextChanged { text, _, _, _ ->
            minSurface = text.toString().toIntOrNull()
        }

        activity_search_edit_surface_max.doOnTextChanged { text, _, _, _ ->
            maxSurface = text.toString().toIntOrNull()
        }

        // Show the AlertDialog to choose the points of interest of the real estate
        activity_search_edit_poi.setOnClickListener {
            myUtils.openPOIDialogFragment(activity_search_edit_poi, supportFragmentManager)
        }

        activity_search_edit_district.doOnTextChanged { text, _, _, _ ->
            district = text.toString()
        }

        // Show the AlertDialog to choose the entry date of the real estate
        activity_search_edit_entry_date.setOnClickListener {
            myUtils.openDateDialogFragment(activity_search_edit_entry_date, supportFragmentManager)
        }

        // Show the AlertDialog to choose the sale date of the real estate
        activity_search_edit_sale_date.setOnClickListener {
            myUtils.openDateDialogFragment(activity_search_edit_sale_date, supportFragmentManager)
        }

        activity_search_button.setOnClickListener {
            searchItemWithPictures()
        }

        activity_search_edit_picture.doOnTextChanged { text, _, _, _ ->
            pictureNumber = text.toString().toIntOrNull()
        }

    }

    //----------------------------------------------------------------------------------
    // Configure design

    private fun configureToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        // Set the Toolbar
        setSupportActionBar(toolbar)
        // Get a support ActionBar corresponding to this toolbar
        val actionBar = supportActionBar
        // Enable the Up button
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Respond to the action bar's Up/Home button
            android.R.id.home ->
                myUtils.showShortToastMessage(this, R.string.search_canceled)
        }
        return super.onOptionsItemSelected(item)
    }

    //----------------------------------------------------------------------------------
    // Implement listeners from the DialogFragments

    override fun onPropertyChosen(propertyChosen: EditText?) {
        type = propertyChosen?.text.toString()
    }

    override fun onPOIChosen(POIChosen: ArrayList<String>?) {
        pointsOfInterest = POIChosen
    }

    override fun onDateChosen(editTextChosen: EditText?) {
        val dateOfEntry: Date?
        val dateOfSale: Date?

        if (editTextChosen?.text.toString() != "") {
            when (editTextChosen) {
                activity_search_edit_entry_date -> entryDate = editTextChosen?.text.toString()
                activity_search_edit_sale_date -> saleDate = editTextChosen?.text.toString()
            }
        } else {
            when (editTextChosen) {
                activity_search_edit_entry_date -> entryDate = null
                activity_search_edit_sale_date -> saleDate = null
            }
        }
        // Parse String from EditText to Date then compare the two dates
        if (!entryDate.isNullOrEmpty() && !saleDate.isNullOrEmpty()) {
            val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            dateOfEntry = sdf.parse(entryDate!!)
            dateOfSale = sdf.parse(saleDate!!)

            // The sale date cannot be earlier than the entry date
            if (dateOfSale.before(dateOfEntry)) {
                myUtils.showShortToastMessage(this, R.string.sale_date_earlier_entry_date)
                activity_search_edit_entry_date.text.clear()
                activity_search_edit_sale_date.text.clear()
                entryDate = null
                saleDate = null
            }
        }
    }

    //----------------------------------------------------------------------------------

    private fun searchItemWithPictures() {
        Log.i(TAG, "type = $type, minPrice = $minPrice, maxPrice = $maxPrice, minSurface = $minSurface," +
                "maxSurface = $maxSurface, pointsOfInterest = $pointsOfInterest, district = $district," +
                " entryDate = $entryDate, saleDate = $saleDate, pictureNumber = $pictureNumber")
    }

}
