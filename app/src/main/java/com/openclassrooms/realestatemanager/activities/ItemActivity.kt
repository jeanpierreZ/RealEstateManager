package com.openclassrooms.realestatemanager.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.ItemPicturesAdapter
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.models.Status
import com.openclassrooms.realestatemanager.models.Type
import com.openclassrooms.realestatemanager.utils.DateDialogFragment
import com.openclassrooms.realestatemanager.utils.POIDialogFragment
import com.openclassrooms.realestatemanager.utils.PropertyDialogFragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ItemActivity : AppCompatActivity(),
        PropertyDialogFragment.OnPropertyChosenListener, POIDialogFragment.OnPOIChosenListener,
        DateDialogFragment.OnDateListener,
        ItemPicturesAdapter.PictureListener, ItemPicturesAdapter.PictureLongClickListener {

    companion object {
        private val TAG = ItemActivity::class.java.simpleName

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
        const val PICTURE_LIST_ITEM = "PICTURE_LIST_ITEM"

        // Request Code for picture
        const val RC_CHOOSE_PHOTO = 100
        const val RC_TAKE_PHOTO = 200
    }

    // Properties of a real estate, Item Model with Address, PointsOfInterest, Status and Type
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

    // Properties of a real estate, Picture Model
    private var pictureList: ArrayList<Picture?> = arrayListOf()
    private var pictureLocation: String? = null

    // File use when user take a photo with the camera
    private var mPhotoFile: File? = null

    // Widget
    private lateinit var editType: EditText
    private lateinit var editPOI: EditText
    private lateinit var editStatus: EditText
    private lateinit var editEntryDate: EditText
    private lateinit var editSaleDate: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemPicturesAdapter: ItemPicturesAdapter

    // Create a charSequence array of the Type Enum and a title
    private val types: Array<CharSequence> =
            arrayOf(Type.DUPLEX.itemType, Type.FLAT.itemType, Type.LOFT.itemType,
                    Type.MANOR.itemType, Type.PENTHOUSE.itemType)
    private val typeTitle = R.string.real_estate_type

    // Create a charSequence array of the Status Enum and a title
    private val statutes: Array<CharSequence> =
            arrayOf(Status.AVAILABLE.disponibility, Status.SOLD.disponibility)
    private val statusTitle = R.string.real_estate_status

    // To compare the dates of entry and sale
    private var dateOfEntry: Date? = null
    private var dateOfSale: Date? = null

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
        val editPictureLocation: EditText = findViewById(R.id.activity_item_edit_picture_location)
        val addPictureButton = findViewById<Button>(R.id.activity_item_button_add_picture)
        val takePictureButton = findViewById<Button>(R.id.activity_item_button_take_picture)
        val saveButton = findViewById<Button>(R.id.activity_item_button_save)

        // Get RecyclerView from layout and serialise it
        recyclerView = findViewById(R.id.activity_item_recycler_view)

        //----------------------------------------------------------------------------------

        configureToolbar()
        configureRecyclerView()
        Log.d(TAG, "ON CREATE pictureList = $pictureList")

        //----------------------------------------------------------------------------------
        // Get data

        // Show the AlertDialog to choose the type of the real estate
        editType.setOnClickListener {
            openPropertyDialogFragment(editType, typeTitle, types)
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
            openPropertyDialogFragment(editStatus, statusTitle, statutes)
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

        editPictureLocation.doOnTextChanged { text, _, _, _ ->
            pictureLocation = text.toString()
        }

        addPictureButton.setOnClickListener {
            addPicture()
        }

        takePictureButton.setOnClickListener {
            takePicture()
        }

        saveButton.setOnClickListener {
            saveItem()
        }
    }

    //----------------------------------------------------------------------------------

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RC_CHOOSE_PHOTO -> {
                    // Uri of picture selected by user
                    val uriPictureSelected = data?.data
                    // Granting temporary permissions to the Uri
                    this.grantUriPermission(BuildConfig.APPLICATION_ID, uriPictureSelected, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    // Create a Picture model with data
                    val picture = Picture(null, pictureLocation, uriPictureSelected, null)
                    // Add Picture to a list
                    pictureList.add(picture)
                    Log.d(TAG, "ON ACTIVITY RESULT pictureList = $pictureList")
                    updatePictureList(pictureList)
                }

                RC_TAKE_PHOTO -> {
                    // Create a Picture model with the file created
                    val pictureTaken = Picture(null, pictureLocation, mPhotoFile?.toUri(), null)
                    // Add Picture to a list
                    pictureList.add(pictureTaken)
                    Log.d(TAG, "ON ACTIVITY RESULT pictureList = $pictureList")
                    updatePictureList(pictureList)
                }
            }
        }
    }

    //----------------------------------------------------------------------------------
    // Configure Toolbar, RecyclerView, Adapter & LayoutManager

    private fun configureToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        // Set the Toolbar
        setSupportActionBar(toolbar)
        // Get a support ActionBar corresponding to this toolbar
        val actionBar = supportActionBar
        // Enable the Up button
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of pictures
        itemPicturesAdapter = ItemPicturesAdapter(pictureList, Glide.with(this), this, this)
        // Attach the adapter to the recyclerView to populate pictures
        recyclerView.adapter = itemPicturesAdapter
        // Set layout manager to position the pictures
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun updatePictureList(updateList: ArrayList<Picture?>) {
        itemPicturesAdapter.setPictures(updateList)
    }

    //----------------------------------------------------------------------------------
    // Configure DialogFragments

    private fun openPOIDialogFragment() {
        val pOIDialogFragment = POIDialogFragment(editPOI)
        pOIDialogFragment.show(supportFragmentManager, "pOIDialogFragment")
    }

    private fun openPropertyDialogFragment(editText: EditText, title: Int, list: Array<CharSequence>) {
        val propertyDialogFragment = PropertyDialogFragment(editText, title, list)
        propertyDialogFragment.show(supportFragmentManager, "propertyDialogFragment")
    }

    private fun openDateDialogFragment(editDate: EditText) {
        val dateDialogFragment = DateDialogFragment(editDate)
        dateDialogFragment.show(supportFragmentManager, "dateDialogFragment")
    }

    //----------------------------------------------------------------------------------
    // Private methods for Buttons

    private fun addPicture() {
        if (pictureLocation != null && pictureLocation?.isNotEmpty()!!) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, RC_CHOOSE_PHOTO)
        } else {
            Toast.makeText(this, getString(R.string.no_picture_location), Toast.LENGTH_SHORT).show()
        }
    }

    // Create an image file and use it when user take a photo
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        )
        // .apply {
        // Save a file: path for use with ACTION_VIEW intents
        // val currentPhotoPath = absolutePath
        // }
    }

    private fun takePicture() {
        if (pictureLocation != null && pictureLocation?.isNotEmpty()!!) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        Log.e(TAG, "$ex")
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                                this,
                                BuildConfig.APPLICATION_ID + ".fileprovider",
                                it)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, RC_TAKE_PHOTO)
                    }
                    // File use in onActivityResult
                    mPhotoFile = photoFile
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.no_picture_location), Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveItem() {
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
        replyIntent.putParcelableArrayListExtra(PICTURE_LIST_ITEM, pictureList)

        setResult(Activity.RESULT_OK, replyIntent)
        finish()
    }

    //----------------------------------------------------------------------------------
    // Implement listener from the DialogFragments to fetch data of the real estate

    override fun onPropertyChosen(propertyChosen: EditText?) {
        if (propertyChosen != null) {
            if (editType == propertyChosen) {
                type = propertyChosen.text.toString()
            } else {
                status = propertyChosen.text.toString()
            }
        }
    }

    override fun onPOIChosen(POIChosen: ArrayList<String>?) {
        pointsOfInterest = POIChosen
    }

    override fun onDateChosen(editTextChosen: EditText?) {
        if (editTextChosen?.text.toString() != "") {
            when (editTextChosen) {
                editEntryDate -> entryDate = editTextChosen.text.toString()
                editSaleDate -> saleDate = editTextChosen.text.toString()
            }
        } else {
            when (editTextChosen) {
                editEntryDate -> entryDate = null
                editSaleDate -> saleDate = null
            }
        }

        // Parse String from EditText to Date then compare the two dates
        if (entryDate != null && entryDate != "" && saleDate != null && saleDate != "") {
            val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            dateOfEntry = sdf.parse(entryDate!!)
            dateOfSale = sdf.parse(saleDate!!)

            // The sale date cannot be earlier than the entry date
            if (dateOfSale!!.before(dateOfEntry)) {
                Toast.makeText(this, getString(R.string.sale_date_earlier_entry_date), Toast.LENGTH_LONG).show()
                editEntryDate.text.clear()
                editSaleDate.text.clear()
                entryDate = null
                saleDate = null
            }
        }
    }

    override fun onClickPicture(position: Int) {
        // Do nothing
    }

    override fun onLongClickItem(position: Int) {
        // Get the picture object with the position in the RecyclerView
        val picture: Picture? = itemPicturesAdapter.getPosition(position)

        // Create an AlertDialog to request deletion of the picture
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.delete_picture))
        builder.apply {
            setPositiveButton(android.R.string.ok) { _, _ ->
                // Remove the picture object from the list to save
                pictureList.remove(picture)
                // Use notifyItemRemoved instead of the updatePictureList() method to enjoy animation
                itemPicturesAdapter.notifyItemRemoved(position)
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}
