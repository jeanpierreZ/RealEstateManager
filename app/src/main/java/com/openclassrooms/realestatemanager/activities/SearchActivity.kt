package com.openclassrooms.realestatemanager.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivitySearchBinding
import com.openclassrooms.realestatemanager.models.Type
import com.openclassrooms.realestatemanager.utils.MyUtils
import com.openclassrooms.realestatemanager.utils.dialogfragments.DateDialogFragment
import com.openclassrooms.realestatemanager.utils.dialogfragments.POIDialogFragment
import com.openclassrooms.realestatemanager.utils.dialogfragments.PropertyDialogFragment
import com.openclassrooms.realestatemanager.views.viewmodels.RealEstateWithMediasViewModel
import kotlinx.android.synthetic.main.toolbar.*
import java.text.SimpleDateFormat
import java.util.*

class SearchActivity : AppCompatActivity(),
        PropertyDialogFragment.OnPropertyChosenListener,
        POIDialogFragment.OnPOIChosenListener,
        DateDialogFragment.OnDateListener {

    companion object {
        private val TAG = SearchActivity::class.java.simpleName

        const val SEARCH_LIST = "SEARCH_LIST"
    }

    private val myUtils = MyUtils()

    // View binding
    private lateinit var binding: ActivitySearchBinding

    // Create a charSequence array of the Type Enum
    private val types: Array<CharSequence> =
            arrayOf(Type.DUPLEX.realEstateType, Type.FLAT.realEstateType, Type.LOFT.realEstateType,
                    Type.MANOR.realEstateType, Type.PENTHOUSE.realEstateType)

    private var type: String? = null
    private var minPrice: Int? = null
    private var maxPrice: Int? = null
    private var minSurface: Int? = null
    private var maxSurface: Int? = null
    private var pointsOfInterest: ArrayList<String>? = null
    private var district: String? = null
    private var entryDate: String? = null
    private var saleDate: String? = null
    private var mediaNumber: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        configureToolbar()

        //----------------------------------------------------------------------------------
        // Get data

        with(binding) {

            // Show the AlertDialog to choose the type of the real estate
            activitySearchEditType.setOnClickListener {
                myUtils.openPropertyDialogFragment(activitySearchEditType, R.string.real_estate_type,
                        types, supportFragmentManager)
            }

            activitySearchEditPriceMin.doOnTextChanged { text, _, _, _ ->
                minPrice = text.toString().toIntOrNull()
            }

            activitySearchEditPriceMax.doOnTextChanged { text, _, _, _ ->
                maxPrice = text.toString().toIntOrNull()
            }

            activitySearchEditSurfaceMin.doOnTextChanged { text, _, _, _ ->
                minSurface = text.toString().toIntOrNull()
            }

            activitySearchEditSurfaceMax.doOnTextChanged { text, _, _, _ ->
                maxSurface = text.toString().toIntOrNull()
            }

            // Show the AlertDialog to choose the points of interest of the real estate
            activitySearchEditPoi.setOnClickListener {
                myUtils.openPOIDialogFragment(activitySearchEditPoi, pointsOfInterest, supportFragmentManager)
            }

            activitySearchEditDistrict.doOnTextChanged { text, _, _, _ ->
                district = text.toString()
            }

            // Show the AlertDialog to choose the entry date of the real estate
            activitySearchEditEntryDate.setOnClickListener {
                myUtils.openDateDialogFragment(activitySearchEditEntryDate, supportFragmentManager)
            }

            // Show the AlertDialog to choose the sale date of the real estate
            activitySearchEditSaleDate.setOnClickListener {
                myUtils.openDateDialogFragment(activitySearchEditSaleDate, supportFragmentManager)
            }

            activitySearchEditMedia.doOnTextChanged { text, _, _, _ ->
                mediaNumber = text.toString().toIntOrNull()
            }

            activitySearchButton.setOnClickListener {
                searchRealEstateWithMedias()
            }
        }
    }

    //----------------------------------------------------------------------------------
    // Configure design

    private fun configureToolbar() {
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
                binding.activitySearchEditEntryDate -> entryDate = editTextChosen.text.toString()
                binding.activitySearchEditSaleDate -> saleDate = editTextChosen.text.toString()
            }
        } else {
            when (editTextChosen) {
                binding.activitySearchEditEntryDate -> entryDate = null
                binding.activitySearchEditSaleDate -> saleDate = null
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
                binding.activitySearchEditEntryDate.text.clear()
                binding.activitySearchEditSaleDate.text.clear()
                entryDate = null
                saleDate = null
            }
        }
    }

    //----------------------------------------------------------------------------------

    private fun searchRealEstateWithMedias() {
        Log.i(TAG, "type = $type, minPrice = $minPrice, maxPrice = $maxPrice, minSurface = $minSurface," +
                " maxSurface = $maxSurface, pointsOfInterest = $pointsOfInterest, district = $district," +
                " entryDate = $entryDate, saleDate = $saleDate, pictureNumber = $mediaNumber")

        // Parameters to query the dao
        var baseQuery = "SELECT * , COUNT(mediaId) AS mediaNumber FROM real_estate_table JOIN media_table ON realEstateId = id"
        val bindArgs = arrayListOf<Any>()
        // To check if the base query already contains "WHEN"
        var queryContainsWhere = false

        if (!type.isNullOrEmpty()) {
            baseQuery = baseQuery.plus(" WHERE type = ?")
            queryContainsWhere = true
            bindArgs.add(type!!)
        }

        if (minPrice != null) {
            if (queryContainsWhere) {
                baseQuery += " AND"
            } else {
                baseQuery += " WHERE"
                queryContainsWhere = true
            }
            baseQuery = baseQuery.plus(" price >= ?")
            bindArgs.add(minPrice!!)
        }

        if (maxPrice != null) {
            if (queryContainsWhere) {
                baseQuery += " AND"
            } else {
                baseQuery += " WHERE"
                queryContainsWhere = true
            }
            baseQuery = baseQuery.plus(" price <= ?")
            bindArgs.add(maxPrice!!)
        }

        if (minSurface != null) {
            if (queryContainsWhere) {
                baseQuery += " AND"
            } else {
                baseQuery += " WHERE"
                queryContainsWhere = true
            }
            baseQuery = baseQuery.plus(" surface >= ?")
            bindArgs.add(minSurface!!)
        }

        if (maxSurface != null) {
            if (queryContainsWhere) {
                baseQuery += " AND"
            } else {
                baseQuery += " WHERE"
                queryContainsWhere = true
            }
            baseQuery = baseQuery.plus(" surface <= ?")
            bindArgs.add(maxSurface!!)
        }

        if (!pointsOfInterest.isNullOrEmpty()) {
            if (queryContainsWhere) {
                baseQuery += " AND"
            } else {
                baseQuery += " WHERE"
                queryContainsWhere = true
            }

            for (poi in pointsOfInterest!!) {
                baseQuery = baseQuery.plus(" pointsOfInterest LIKE ? AND")
                val likePoi = "%$poi%"
                bindArgs.add(likePoi)
            }
            baseQuery = baseQuery.removeSuffix(" AND")
        }

        if (!district.isNullOrEmpty() || !district.isNullOrBlank()) {
            if (queryContainsWhere) {
                baseQuery += " AND"
            } else {
                baseQuery += " WHERE"
                queryContainsWhere = true
            }
            baseQuery = baseQuery.plus(" district = ?")
            bindArgs.add(district!!)
        }

        if (!entryDate.isNullOrEmpty()) {
            if (queryContainsWhere) {
                baseQuery += " AND"
            } else {
                baseQuery += " WHERE"
                queryContainsWhere = true
            }
            baseQuery = baseQuery.plus(" entryDate >= ?")
            bindArgs.add(entryDate!!)
        }

        if (!saleDate.isNullOrEmpty()) {
            baseQuery += if (queryContainsWhere) {
                " AND"
            } else {
                " WHERE"
            }
            baseQuery = baseQuery.plus(" saleDate >= ?")
            bindArgs.add(saleDate!!)
        }

        mediaNumber = mediaNumber ?: 0
        baseQuery = baseQuery.plus(" GROUP BY id HAVING mediaNumber >= ?")
        bindArgs.add(mediaNumber!!)

        // Sort the query according to the last properties added
        baseQuery = baseQuery.plus(" ORDER BY id DESC")
        Log.d(TAG, "baseQuery = $baseQuery")

        val query = SimpleSQLiteQuery(baseQuery, bindArgs.toArray())

        val realEstateWithMediasViewModel = ViewModelProvider(this).get(RealEstateWithMediasViewModel::class.java)
        val searchIntent = Intent()

        realEstateWithMediasViewModel.getRealEstateWithMediasFromSearch(query).observe(
                this, androidx.lifecycle.Observer { realEstateWithMedias ->
            Log.d(TAG, "search result = $realEstateWithMedias")

            if (!realEstateWithMedias.isNullOrEmpty()) {
                searchIntent.putParcelableArrayListExtra(SEARCH_LIST, realEstateWithMedias as ArrayList)
                this.setResult(Activity.RESULT_OK, searchIntent)
                this.finish()

            } else {
                myUtils.showShortToastMessage(this, R.string.no_results_search)
            }
        })
    }

}
