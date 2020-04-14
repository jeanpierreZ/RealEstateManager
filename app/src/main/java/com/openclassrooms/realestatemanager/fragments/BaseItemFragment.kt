package com.openclassrooms.realestatemanager.fragments

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
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

/**
 * A simple [Fragment] subclass.
 */
abstract class BaseItemFragment : Fragment(),
        ItemPicturesAdapter.PictureListener, ItemPicturesAdapter.PictureLongClickListener {

    companion object {
        private val TAG = BaseItemFragment::class.java.simpleName

        // Keys for item attributes
        const val ID_ITEM = "ID_ITEM"
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
        const val LATITUDE_ITEM = "LATITUDE_ITEM"
        const val LONGITUDE_ITEM = "LONGITUDE_ITEM"
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
    private val saveItemIntent = Intent()
    protected var itemId: Long? = null
    protected var type: String? = null
    protected var price: Int? = null
    protected var surface: Int? = null
    protected var roomsNumber: Int? = null
    protected var bathroomsNumber: Int? = null
    protected var bedroomsNumber: Int? = null
    protected var pointsOfInterest: ArrayList<String>? = null
    protected var streetNumber: String? = null
    protected var street: String? = null
    protected var apartmentNumber: String? = null
    protected var district: String? = null
    protected var city: String? = null
    protected var postalCode: String? = null
    protected var country: String? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    protected var description: String? = null
    protected var status: String? = null
    protected var entryDate: String? = null
    protected var saleDate: String? = null
    protected var agent: String? = null

    // Properties of a real estate, Picture Model
    private var pictureList: ArrayList<Picture?> = arrayListOf()
    private var pictureLocation: String? = null

    // File use when user take a photo with the camera
    private var mPhotoFile: File? = null

    // Widget
    protected lateinit var titleText: TextView
    protected var editType: EditText? = null
    protected lateinit var editPrice: EditText
    protected lateinit var editSurface: EditText
    protected lateinit var editRooms: EditText
    protected lateinit var editBathrooms: EditText
    protected lateinit var editBedrooms: EditText
    protected lateinit var editPOI: EditText
    protected lateinit var editStreetNumber: EditText
    protected lateinit var editStreet: EditText
    protected lateinit var editApartmentNumber: EditText
    protected lateinit var editDistrict: EditText
    protected lateinit var editCity: EditText
    protected lateinit var editPostalCode: EditText
    protected lateinit var editCountry: EditText
    protected lateinit var editDescription: EditText
    protected lateinit var editStatus: EditText
    protected lateinit var editEntryDate: EditText
    protected lateinit var editSaleDate: EditText
    protected lateinit var editAgent: EditText
    private lateinit var editPictureLocation: EditText

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemPicturesAdapter: ItemPicturesAdapter

    // Create a charSequence array of the Type Enum and a title
    private val types: Array<CharSequence> =
            arrayOf(Type.DUPLEX.itemType, Type.FLAT.itemType, Type.LOFT.itemType,
                    Type.MANOR.itemType, Type.PENTHOUSE.itemType)
    private val typeTitle = R.string.real_estate_type

    // Create a charSequence array of the Status Enum and a title
    private val statutes: Array<CharSequence> =
            arrayOf(Status.AVAILABLE.availability, Status.SOLD.availability)
    private val statusTitle = R.string.real_estate_status

    // To compare the dates of entry and sale
    private var dateOfEntry: Date? = null
    private var dateOfSale: Date? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val fragmentView: View = inflater.inflate(R.layout.fragment_base_item, container, false)

        titleText = fragmentView.findViewById(R.id.fragment_base_item_title)
        editType = fragmentView.findViewById(R.id.fragment_base_item_edit_type)
        editPrice = fragmentView.findViewById(R.id.fragment_base_item_edit_price)
        editSurface = fragmentView.findViewById(R.id.fragment_base_item_edit_surface)
        editRooms = fragmentView.findViewById(R.id.fragment_base_item_edit_rooms)
        editBathrooms = fragmentView.findViewById(R.id.fragment_base_item_edit_bathrooms)
        editBedrooms = fragmentView.findViewById(R.id.fragment_base_item_edit_bedrooms)
        editPOI = fragmentView.findViewById(R.id.fragment_base_item_edit_poi)
        editStreetNumber = fragmentView.findViewById(R.id.fragment_base_item_edit_street_number)
        editStreet = fragmentView.findViewById(R.id.fragment_base_item_edit_street)
        editApartmentNumber = fragmentView.findViewById(R.id.fragment_base_item_edit_apartment_number)
        editDistrict = fragmentView.findViewById(R.id.fragment_base_item_edit_district)
        editCity = fragmentView.findViewById(R.id.fragment_base_item_edit_city)
        editPostalCode = fragmentView.findViewById(R.id.fragment_base_item_edit_postal_code)
        editCountry = fragmentView.findViewById(R.id.fragment_base_item_edit_country)
        editDescription = fragmentView.findViewById(R.id.fragment_base_item_edit_description)
        editStatus = fragmentView.findViewById(R.id.fragment_base_item_edit_status)
        editEntryDate = fragmentView.findViewById(R.id.fragment_base_item_edit_entry_date)
        editSaleDate = fragmentView.findViewById(R.id.fragment_base_item_edit_sale_date)
        editAgent = fragmentView.findViewById(R.id.fragment_base_item_edit_agent)
        editPictureLocation = fragmentView.findViewById(R.id.fragment_base_item_edit_picture_location)
        val addPictureButton = fragmentView.findViewById<Button>(R.id.fragment_base_item_button_add_picture)
        val takePictureButton = fragmentView.findViewById<Button>(R.id.fragment_base_item_button_take_picture)
        val saveButton = fragmentView.findViewById<Button>(R.id.fragment_base_item_button_save)
        recyclerView = fragmentView.findViewById(R.id.fragment_base_item_recycler_view)

        configureRecyclerView()

        Log.d(TAG, "ON CREATE pictureList = $pictureList")

        //----------------------------------------------------------------------------------
        // Get data

        // Show the AlertDialog to choose the type of the real estate
        editType?.setOnClickListener {
            openPropertyDialogFragment(editType!!, typeTitle, types)
        }

        editPrice.doOnTextChanged { text, _, _, _ ->
            price = text.toString().toIntOrNull()
        }

        editSurface.doOnTextChanged { text, _, _, _ ->
            surface = text.toString().toIntOrNull()
        }

        editRooms.doOnTextChanged { text, _, _, _ ->
            roomsNumber = text.toString().toIntOrNull()
        }

        editBathrooms.doOnTextChanged { text, _, _, _ ->
            bathroomsNumber = text.toString().toIntOrNull()
        }

        editBedrooms.doOnTextChanged { text, _, _, _ ->
            bedroomsNumber = text.toString().toIntOrNull()
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
            saveItemWithPictures()
        }

        return fragmentView
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
                    activity?.grantUriPermission(BuildConfig.APPLICATION_ID, uriPictureSelected, Intent.FLAG_GRANT_READ_URI_PERMISSION)
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
    // Configure RecyclerView, Adapter & LayoutManager

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of pictures
        itemPicturesAdapter = ItemPicturesAdapter(pictureList, Glide.with(this), this, this)
        // Attach the adapter to the recyclerView to populate pictures
        recyclerView.adapter = itemPicturesAdapter
        // Set layout manager to position the pictures
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }

    protected fun updatePictureList(updateList: ArrayList<Picture?>) {
        itemPicturesAdapter.setPictures(updateList)
    }

    //----------------------------------------------------------------------------------
    // Configure DialogFragments

    private fun openPOIDialogFragment() {
        val pOIDialogFragment = POIDialogFragment(editPOI)
        pOIDialogFragment.show(requireActivity().supportFragmentManager, "pOIDialogFragment")
    }

    private fun openPropertyDialogFragment(editText: EditText, title: Int, list: Array<CharSequence>) {
        val propertyDialogFragment = PropertyDialogFragment(editText, title, list)
        propertyDialogFragment.show(requireActivity().supportFragmentManager, "propertyDialogFragment")
    }

    private fun openDateDialogFragment(editDate: EditText) {
        val dateDialogFragment = DateDialogFragment(editDate)
        dateDialogFragment.show(requireActivity().supportFragmentManager, "dateDialogFragment")
    }

    //----------------------------------------------------------------------------------
    // Private methods for Pictures and data

    private fun addPicture() {
        if (pictureLocation != null && pictureLocation?.isNotEmpty()!!) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, RC_CHOOSE_PHOTO)
        } else {
            Toast.makeText(activity, getString(R.string.no_picture_location), Toast.LENGTH_SHORT).show()
        }
    }

    // Create an image file and use it when user take a photo
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        )
        /*
         .apply {
         Save a file: path for use with ACTION_VIEW intents
         val currentPhotoPath = absolutePath
         }
        */
    }

    private fun takePicture() {
        if (pictureLocation != null && pictureLocation?.isNotEmpty()!!) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
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
                                requireActivity(),
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
            Toast.makeText(activity, getString(R.string.no_picture_location), Toast.LENGTH_SHORT).show()
        }
    }

    private fun storeLatLng() {
        val streetNumberFormat = streetNumber ?: ""
        val streetFormat = street ?: ""
        val districtFormat = district ?: ""
        val postalCodeFormat = postalCode ?: ""
        val cityFormat = city ?: ""
        val countryFormat = country ?: ""
        val fullAddress = String.format("$streetNumberFormat $streetFormat $districtFormat $postalCodeFormat $cityFormat $countryFormat")
        val geocoder = Geocoder(activity)
        val addresses: List<Address>
        addresses = geocoder.getFromLocationName(fullAddress, 1)

        if (addresses.isNotEmpty()) {
            latitude = addresses[0].latitude
            longitude = addresses[0].longitude
        }
        saveItemIntent.putExtra(LATITUDE_ITEM, latitude)
        saveItemIntent.putExtra(LONGITUDE_ITEM, longitude)
    }

    private fun saveItemWithPictures() {
        storeLatLng()
//        val saveItemIntent = Intent()
        saveItemIntent.putExtra(ID_ITEM, itemId)
        saveItemIntent.putExtra(TYPE_ITEM, type)
        saveItemIntent.putExtra(PRICE_ITEM, price)
        saveItemIntent.putExtra(SURFACE_ITEM, surface)
        saveItemIntent.putExtra(ROOMS_ITEM, roomsNumber)
        saveItemIntent.putExtra(BATHROOMS_ITEM, bathroomsNumber)
        saveItemIntent.putExtra(BEDROOMS_ITEM, bedroomsNumber)
        saveItemIntent.putStringArrayListExtra(POI_ITEM, pointsOfInterest)
        saveItemIntent.putExtra(STREET_NUMBER_ITEM, streetNumber)
        saveItemIntent.putExtra(STREET_ITEM, street)
        saveItemIntent.putExtra(APARTMENT_NUMBER_ITEM, apartmentNumber)
        saveItemIntent.putExtra(DISTRICT_ITEM, district)
        saveItemIntent.putExtra(CITY_ITEM, city)
        saveItemIntent.putExtra(POSTAL_CODE_ITEM, postalCode)
        saveItemIntent.putExtra(COUNTRY_ITEM, country)
        saveItemIntent.putExtra(DESCRIPTION_ITEM, description)
        saveItemIntent.putExtra(STATUS_ITEM, status)
        saveItemIntent.putExtra(ENTRY_DATE_ITEM, entryDate)
        saveItemIntent.putExtra(SALE_DATE_ITEM, saleDate)
        saveItemIntent.putExtra(AGENT_ITEM, agent)
        saveItemIntent.putParcelableArrayListExtra(PICTURE_LIST_ITEM, pictureList)

        activity?.setResult(Activity.RESULT_OK, saveItemIntent)
        activity?.finish()
    }

    //----------------------------------------------------------------------------------
    // Use DialogFragments data received in ItemActivity

    fun setPropertyChosen(propertyChosen: EditText?) {
        if (propertyChosen != null) {
            if (editType == propertyChosen) {
                type = propertyChosen.text.toString()
            } else {
                status = propertyChosen.text.toString()
            }
        }
    }

    fun setPOIChosen(POIChosen: ArrayList<String>?) {
        pointsOfInterest = POIChosen
    }

    fun setDateChosen(editTextChosen: EditText?) {
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
                Toast.makeText(activity, getString(R.string.sale_date_earlier_entry_date), Toast.LENGTH_LONG).show()
                editEntryDate.text.clear()
                editSaleDate.text.clear()
                entryDate = null
                saleDate = null
            }
        }
    }

    //----------------------------------------------------------------------------------
    // Listeners in ItemPicturesAdapter

    override fun onClickPicture(position: Int) {
        // Do nothing
    }

    override fun onLongClickItem(position: Int) {
        // Get the picture object with the position in the RecyclerView
        val picture: Picture? = itemPicturesAdapter.getPosition(position)

        // Create an AlertDialog to request deletion of the picture
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
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
