package com.openclassrooms.realestatemanager.fragments

import android.Manifest.permission.*
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
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.ItemPicturesAdapter
import com.openclassrooms.realestatemanager.databinding.FragmentBaseItemBinding
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.models.Status
import com.openclassrooms.realestatemanager.models.Type
import com.openclassrooms.realestatemanager.utils.MyUtils
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
abstract class BaseItemFragment : Fragment(), EasyPermissions.PermissionCallbacks,
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

        // Keys for permissions
        val READ_PERM = arrayOf(READ_EXTERNAL_STORAGE)
        val WRITE_EXT_STORAGE_AND_CAMERA_PERMS = arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA)

        // Request codes for permissions
        const val RC_READ_PERM = 333
        const val RC_WRITE_EXT_STORAGE_AND_CAMERA_PERMS = 444

        // Request codes for picture
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
    protected var pictureList: ArrayList<Picture?> = arrayListOf()
    private var pictureDescription: String? = null

    // File use when user take a photo with the camera
    private var mPhotoFile: File? = null

    // Adapter for recycler view
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

    private val myUtils = MyUtils()

    // View binding
    private var _binding: FragmentBaseItemBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    protected val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        _binding = FragmentBaseItemBinding.inflate(inflater, container, false)
        val view = binding.root

        configureRecyclerView()

        Log.d(TAG, "ON CREATE pictureList = $pictureList")

        //----------------------------------------------------------------------------------
        // Get data

        // Show the AlertDialog to choose the type of the real estate
        binding.fragmentBaseItemEditType.setOnClickListener {
            myUtils.openPropertyDialogFragment(binding.fragmentBaseItemEditType, typeTitle, types,
                    requireActivity().supportFragmentManager)
        }

        binding.fragmentBaseItemEditPrice.doOnTextChanged { text, _, _, _ ->
            price = text.toString().toIntOrNull()
        }

        binding.fragmentBaseItemEditSurface.doOnTextChanged { text, _, _, _ ->
            surface = text.toString().toIntOrNull()
        }

        binding.fragmentBaseItemEditRooms.doOnTextChanged { text, _, _, _ ->
            roomsNumber = text.toString().toIntOrNull()
        }

        binding.fragmentBaseItemEditBathrooms.doOnTextChanged { text, _, _, _ ->
            bathroomsNumber = text.toString().toIntOrNull()
        }

        binding.fragmentBaseItemEditBedrooms.doOnTextChanged { text, _, _, _ ->
            bedroomsNumber = text.toString().toIntOrNull()
        }

        // Show the AlertDialog to choose the points of interest of the real estate
        binding.fragmentBaseItemEditPoi.setOnClickListener {
            myUtils.openPOIDialogFragment(binding.fragmentBaseItemEditPoi, pointsOfInterest,
                    requireActivity().supportFragmentManager)
        }

        binding.fragmentBaseItemEditStreetNumber.doOnTextChanged { text, _, _, _ ->
            streetNumber = text.toString()
        }

        binding.fragmentBaseItemEditStreet.doOnTextChanged { text, _, _, _ ->
            street = text.toString()
        }

        binding.fragmentBaseItemEditApartmentNumber.doOnTextChanged { text, _, _, _ ->
            apartmentNumber = text.toString()
        }

        binding.fragmentBaseItemEditDistrict.doOnTextChanged { text, _, _, _ ->
            district = text.toString()
        }

        binding.fragmentBaseItemEditCity.doOnTextChanged { text, _, _, _ ->
            city = text.toString()
        }

        binding.fragmentBaseItemEditPostalCode.doOnTextChanged { text, _, _, _ ->
            postalCode = text.toString()
        }

        binding.fragmentBaseItemEditCountry.doOnTextChanged { text, _, _, _ ->
            country = text.toString()
        }

        binding.fragmentBaseItemEditDescription.doOnTextChanged { text, _, _, _ ->
            description = text.toString()
        }

        // Show the AlertDialog to choose the status of the real estate
        binding.fragmentBaseItemEditStatus.setOnClickListener {
            myUtils.openPropertyDialogFragment(binding.fragmentBaseItemEditStatus, statusTitle, statutes,
                    requireActivity().supportFragmentManager)
        }

        // Show the AlertDialog to choose the entry date of the real estate
        binding.fragmentBaseItemEditEntryDate.setOnClickListener {
            myUtils.openDateDialogFragment(binding.fragmentBaseItemEditEntryDate,
                    requireActivity().supportFragmentManager)
        }

        // Show the AlertDialog to choose the sale date of the real estate
        binding.fragmentBaseItemEditSaleDate.setOnClickListener {
            myUtils.openDateDialogFragment(binding.fragmentBaseItemEditSaleDate,
                    requireActivity().supportFragmentManager)
        }

        binding.fragmentBaseItemEditAgent.doOnTextChanged { text, _, _, _ ->
            agent = text.toString()
        }

        binding.fragmentBaseItemEditPictureDescription.doOnTextChanged { text, _, _, _ ->
            pictureDescription = text.toString()
        }

        binding.fragmentBaseItemButtonAddPicture.setOnClickListener {
            addPicture()
        }

        binding.fragmentBaseItemButtonTakePicture.setOnClickListener {
            takePicture()
        }

        binding.fragmentBaseItemButtonSave.setOnClickListener {
            saveItemWithPictures()
        }

        return view
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
                    activity?.grantUriPermission(BuildConfig.APPLICATION_ID, uriPictureSelected,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    // Create a Picture model with data
                    val picture = Picture(null, pictureDescription, uriPictureSelected, null)
                    // Add Picture to a list
                    pictureList.add(picture)
                    Log.d(TAG, "ON ACTIVITY RESULT pictureList = $pictureList")
                    updatePictureList(pictureList)
                }

                RC_TAKE_PHOTO -> {
                    // Create a Picture model with the file created
                    val pictureTaken = Picture(null, pictureDescription, mPhotoFile?.toUri(), null)
                    // Add Picture to a list
                    pictureList.add(pictureTaken)
                    Log.d(TAG, "ON ACTIVITY RESULT pictureList = $pictureList")
                    updatePictureList(pictureList)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerView, Adapter & LayoutManager

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of pictures
        itemPicturesAdapter = ItemPicturesAdapter(pictureList, Glide.with(this), this, this)
        // Attach the adapter to the recyclerView to populate pictures
        binding.fragmentBaseItemRecyclerView.adapter = itemPicturesAdapter
        // Set layout manager to position the pictures
        binding.fragmentBaseItemRecyclerView.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }

    protected fun updatePictureList(updateList: ArrayList<Picture?>) {
        itemPicturesAdapter.setPictures(updateList)
    }

    //----------------------------------------------------------------------------------
    // Private methods for Pictures and data

    @AfterPermissionGranted(RC_READ_PERM)
    private fun addPicture() {
        if (activity?.let { EasyPermissions.hasPermissions(it, *READ_PERM) }!!) {
            if (pictureDescription.isNullOrEmpty() || pictureDescription.isNullOrBlank()) {
                activity?.let { myUtils.showShortToastMessage(it, R.string.no_picture_description) }
            } else {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, RC_CHOOSE_PHOTO)
            }
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_read_permission_access),
                    RC_READ_PERM, *READ_PERM)
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

    @AfterPermissionGranted(RC_WRITE_EXT_STORAGE_AND_CAMERA_PERMS)
    private fun takePicture() {
        if (activity?.let { EasyPermissions.hasPermissions(it, *WRITE_EXT_STORAGE_AND_CAMERA_PERMS) }!!) {
            if (pictureDescription.isNullOrEmpty() || pictureDescription.isNullOrBlank()) {
                activity?.let { myUtils.showShortToastMessage(it, R.string.no_picture_description) }
            } else {
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
            }
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_write_and_camera_permission_access),
                    RC_WRITE_EXT_STORAGE_AND_CAMERA_PERMS, *WRITE_EXT_STORAGE_AND_CAMERA_PERMS)
        }
    }

    private fun storeLatLng() {
        val streetNumberFormat = streetNumber ?: ""
        val streetFormat = street ?: ""
        val districtFormat = district ?: ""
        val postalCodeFormat = postalCode ?: ""
        val cityFormat = city ?: ""
        val countryFormat = country ?: ""
        val fullAddress = "$streetNumberFormat $streetFormat $districtFormat $postalCodeFormat $cityFormat $countryFormat"
        //String.format("$streetNumberFormat $streetFormat $districtFormat $postalCodeFormat $cityFormat $countryFormat")
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
            if (binding.fragmentBaseItemEditType == propertyChosen) {
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
        // To compare the dates of entry and sale
        val dateOfEntry: Date?
        val dateOfSale: Date?

        if (editTextChosen?.text.toString() != "") {
            when (editTextChosen) {
                binding.fragmentBaseItemEditEntryDate -> entryDate = editTextChosen.text.toString()
                binding.fragmentBaseItemEditSaleDate -> saleDate = editTextChosen.text.toString()
            }
        } else {
            when (editTextChosen) {
                binding.fragmentBaseItemEditEntryDate -> entryDate = null
                binding.fragmentBaseItemEditSaleDate -> saleDate = null
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
                binding.fragmentBaseItemEditEntryDate.text.clear()
                binding.fragmentBaseItemEditSaleDate.text.clear()
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

    //----------------------------------------------------------------------------------
    // Easy Permissions

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        // If there isn't permission, wait for the user to allow permissions before starting...
    }

}
