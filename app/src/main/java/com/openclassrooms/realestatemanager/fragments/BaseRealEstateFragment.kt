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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.MediaAdapter
import com.openclassrooms.realestatemanager.models.Media
import com.openclassrooms.realestatemanager.models.Status
import com.openclassrooms.realestatemanager.models.Type
import com.openclassrooms.realestatemanager.utils.MyUtils
import com.openclassrooms.realestatemanager.utils.Utils
import kotlinx.android.synthetic.main.fragment_base_real_estate.*
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
abstract class BaseRealEstateFragment : Fragment(),
        EasyPermissions.PermissionCallbacks,
        MediaAdapter.MediaListener,
        MediaAdapter.MediaLongClickListener,
        MediaAdapter.MediaFullScreenListener {

    companion object {
        private val TAG = BaseRealEstateFragment::class.java.simpleName

        // Keys for real estate attributes
        const val ID_REAL_ESTATE = "ID_REAL_ESTATE"
        const val TYPE_REAL_ESTATE = "TYPE_REAL_ESTATE"
        const val PRICE_REAL_ESTATE = "PRICE_REAL_ESTATE"
        const val SURFACE_REAL_ESTATE = "SURFACE_REAL_ESTATE"
        const val ROOMS_REAL_ESTATE = "ROOMS_REAL_ESTATE"
        const val BATHROOMS_REAL_ESTATE = "BATHROOMS_REAL_ESTATE"
        const val BEDROOMS_REAL_ESTATE = "BEDROOMS_REAL_ESTATE"
        const val POI_REAL_ESTATE = "POI_REAL_ESTATE"
        const val STREET_NUMBER_REAL_ESTATE = "STREET_NUMBER_REAL_ESTATE"
        const val STREET_REAL_ESTATE = "STREET_REAL_ESTATE"
        const val APARTMENT_NUMBER_REAL_ESTATE = "APARTMENT_NUMBER_REAL_ESTATE"
        const val DISTRICT_REAL_ESTATE = "DISTRICT_REAL_ESTATE"
        const val CITY_REAL_ESTATE = "CITY_REAL_ESTATE"
        const val POSTAL_CODE_REAL_ESTATE = "POSTAL_CODE_REAL_ESTATE"
        const val COUNTRY_REAL_ESTATE = "COUNTRY_REAL_ESTATE"
        const val LATITUDE_REAL_ESTATE = "LATITUDE_REAL_ESTATE"
        const val LONGITUDE_REAL_ESTATE = "LONGITUDE_REAL_ESTATE"
        const val DESCRIPTION_REAL_ESTATE = "DESCRIPTION_REAL_ESTATE"
        const val STATUS_REAL_ESTATE = "STATUS_REAL_ESTATE"
        const val ENTRY_DATE_REAL_ESTATE = "ENTRY_DATE_REAL_ESTATE"
        const val SALE_DATE_REAL_ESTATE = "SALE_DATE_REAL_ESTATE"
        const val AGENT_REAL_ESTATE = "AGENT_REAL_ESTATE"
        const val MEDIA_LIST_REAL_ESTATE = "MEDIA_LIST_REAL_ESTATE"

        // Keys for permissions
        val READ_PERM = arrayOf(READ_EXTERNAL_STORAGE)
        val WRITE_EXT_STORAGE_AND_CAMERA_PERMS = arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA)

        // Request codes for permissions
        const val RC_READ_PERM = 400
        const val RC_WRITE_EXT_STORAGE_AND_CAMERA_PERMS = 500

        // Request codes for media
        const val RC_CHOOSE_PHOTO = 100
        const val RC_TAKE_PHOTO = 200
        const val RC_VIDEO_CAPTURE = 300
    }

    // Properties of a RealEstate with Address, PointsOfInterest, Status and Type
    private val saveRealEstateIntent = Intent()
    protected var id: Long? = null
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

    // Media properties associated with real estate
    protected var mediaList: ArrayList<Media?> = arrayListOf()
    private var mediaDescription: String? = null

    // File used when user take a photo with the camera
    private var mPhotoFile: File? = null

    private var recyclerView: RecyclerView? = null
    private var mediaAdapter: MediaAdapter? = null
    private lateinit var pager: LinearLayout
    private val isFullScreen = false
    private val isRealEstateActivity = true

    // Create a charSequence array of the Type Enum and a title
    private val types: Array<CharSequence> =
            arrayOf(Type.DUPLEX.realEstateType, Type.FLAT.realEstateType, Type.LOFT.realEstateType,
                    Type.MANOR.realEstateType, Type.PENTHOUSE.realEstateType)
    private val typeTitle = R.string.real_estate_type

    // Create a charSequence array of the Status Enum and a title
    private val statutes: Array<CharSequence> =
            arrayOf(Status.AVAILABLE.availability, Status.SOLD.availability)
    private val statusTitle = R.string.real_estate_status

    private val myUtils = MyUtils()
    private val toSearch = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView: View = inflater.inflate(R.layout.fragment_base_real_estate, container, false)

        recyclerView = fragmentView.findViewById(R.id.fragment_base_real_estate_recycler_view)
        pager = fragmentView.findViewById(R.id.fragment_base_real_estate_pager)

        configureRecyclerView()

        // Alert the user that without connectivity, it is impossible to have the GPS coordinates of the address
        if (!Utils.isInternetAvailable(activity)) {
            impossibleToStoreLatLong()
        }

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Get data

        // Show the AlertDialog to choose the type of the real estate
        fragment_base_real_estate_type.editText?.setOnClickListener {
            myUtils.openPropertyDialogFragment(fragment_base_real_estate_type.editText!!, typeTitle,
                    type, types, requireActivity().supportFragmentManager, toSearch)
        }
        validateType()
        fragment_base_real_estate_type.editText?.doAfterTextChanged { validateType() }

        fragment_base_real_estate_price.editText?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                myUtils.validatePrice(fragment_base_real_estate_price, requireActivity(), s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                price = myUtils.formatPrice(fragment_base_real_estate_price?.editText, this, s)
            }
        })

        fragment_base_real_estate_surface.editText?.doOnTextChanged { text, _, _, _ ->
            surface = text.toString().toIntOrNull()
        }

        fragment_base_real_estate_rooms.editText?.doOnTextChanged { text, _, _, _ ->
            roomsNumber = text.toString().toIntOrNull()
        }

        fragment_base_real_estate_bathrooms.editText?.doOnTextChanged { text, _, _, _ ->
            bathroomsNumber = text.toString().toIntOrNull()
        }

        fragment_base_real_estate_bedrooms.editText?.doOnTextChanged { text, _, _, _ ->
            bedroomsNumber = text.toString().toIntOrNull()
        }

        // Show the AlertDialog to choose the points of interest of the real estate
        fragment_base_real_estate_poi.editText?.setOnClickListener {
            myUtils.openPOIDialogFragment(fragment_base_real_estate_poi.editText!!, pointsOfInterest,
                    requireActivity().supportFragmentManager)
        }

        fragment_base_real_estate_street_number.editText?.doOnTextChanged { text, _, _, _ ->
            streetNumber = text.toString()
        }

        fragment_base_real_estate_street.editText?.doOnTextChanged { text, _, _, _ ->
            street = text.toString()
        }

        fragment_base_real_estate_apartment_number.editText?.doOnTextChanged { text, _, _, _ ->
            apartmentNumber = text.toString()
        }

        fragment_base_real_estate_district.editText?.doOnTextChanged { text, _, _, _ ->
            district = text.toString()
        }

        fragment_base_real_estate_city.editText?.doOnTextChanged { text, _, _, _ ->
            city = text.toString()
        }

        fragment_base_real_estate_postal_code.editText?.doOnTextChanged { text, _, _, _ ->
            postalCode = text.toString()
        }

        fragment_base_real_estate_country.editText?.doOnTextChanged { text, _, _, _ ->
            country = text.toString()
        }

        fragment_base_real_estate_description.editText?.doOnTextChanged { text, _, _, _ ->
            description = text.toString()
        }

        // Show the AlertDialog to choose the status of the real estate
        fragment_base_real_estate_status.editText?.setOnClickListener {
            myUtils.openPropertyDialogFragment(fragment_base_real_estate_status.editText!!, statusTitle,
                    status, statutes, requireActivity().supportFragmentManager, toSearch)
        }

        // Show the AlertDialog to choose the entry date of the real estate
        fragment_base_real_estate_entry_date.editText?.setOnClickListener {
            myUtils.openDateDialogFragment(fragment_base_real_estate_entry_date.editText!!, entryDate,
                    requireActivity().supportFragmentManager)
        }

        // Show the AlertDialog to choose the sale date of the real estate
        fragment_base_real_estate_sale_date.editText?.setOnClickListener {
            myUtils.openDateDialogFragment(fragment_base_real_estate_sale_date.editText!!, saleDate,
                    requireActivity().supportFragmentManager)
        }

        fragment_base_real_estate_agent.editText?.doOnTextChanged { text, _, _, _ ->
            agent = text.toString()
        }

        validateMedia()
        fragment_base_real_estate_media_description.editText?.doOnTextChanged { text, _, _, _ ->
            mediaDescription = text.toString()
        }

        fragment_base_real_estate_button_add_picture.setOnClickListener {
            addPicture()
        }

        fragment_base_real_estate_button_take_picture.setOnClickListener {
            takePicture()
        }

        fragment_base_real_estate_button_take_video.setOnClickListener {
            takeVideo()
        }

        fragment_base_real_estate_button_save.setOnClickListener {
            saveRealEstateWithMedias()
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
                    activity?.grantUriPermission(BuildConfig.APPLICATION_ID, uriPictureSelected,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    val photoFlags = data?.flags?.and((Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
                    if (uriPictureSelected != null && photoFlags != null) {
                        activity?.contentResolver?.takePersistableUriPermission(uriPictureSelected, photoFlags)
                    }
                    // Create a Media model with data
                    val picture = Media(null, mediaDescription, uriPictureSelected, null, null)
                    // Add Picture to a list
                    mediaList.add(picture)
                    Log.d(TAG, "ON ACTIVITY RESULT mediaList = $mediaList")
                    refreshMediaInList(mediaList)
                }

                RC_TAKE_PHOTO -> {
                    // Create a Media model with the file created
                    val pictureTaken = Media(null, mediaDescription, mPhotoFile?.toUri(), null, null)
                    // Add Media to a list
                    mediaList.add(pictureTaken)
                    Log.d(TAG, "ON ACTIVITY RESULT mediaList = $mediaList")
                    refreshMediaInList(mediaList)
                }

                RC_VIDEO_CAPTURE -> {
                    // Uri of video take by user
                    val videoUri: Uri? = data?.data
                    // Create a Media model with data
                    val videoTaken = Media(null, mediaDescription, null, videoUri, null)
                    // Add Media to a list
                    mediaList.add(videoTaken)
                    Log.d(TAG, "ON ACTIVITY RESULT mediaList = $mediaList")
                    refreshMediaInList(mediaList)
                }
            }
        }
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerView, Adapter & LayoutManager

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of media
        mediaAdapter = activity?.let {
            MediaAdapter(mediaList, Glide.with(this), this, this, this, isFullScreen, isRealEstateActivity, it)
        }
        // Attach the adapter to the recyclerView to populate medias
        recyclerView?.adapter = mediaAdapter
        // Set layout manager to position the medias
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        // Check if we are in tablet mode to adapt layout
        if (myUtils.getScreenWidth(requireActivity()) > 720) {
            recyclerView?.setPadding(300, 0, 300, 0)
        }

        // To swipe page after page
        recyclerView?.onFlingListener = null
        PagerSnapHelper().attachToRecyclerView(recyclerView)

        // Update the pagerRecyclerView when switch on an other page
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val position = (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
                myUtils.addPagerToRecyclerView(requireActivity(), mediaList, position, pager)
            }
        })
    }

    protected fun insertMediaInList(updateList: ArrayList<Media?>) {
        mediaAdapter?.setMedias(updateList)
        myUtils.addPagerToRecyclerView(requireActivity(), updateList, 0, pager)
    }

    private fun refreshMediaInList(updateList: ArrayList<Media?>) {
        mediaAdapter?.notifyDataSetChanged()
        val position = updateList.lastIndex
        recyclerView?.scrollToPosition(position)
        // Update pager
        myUtils.addPagerToRecyclerView(requireActivity(), updateList, position, pager)
        validateMedia()
    }

    private fun removeMediaFromListAndRefresh(media: Media, position: Int) {
        var updatedPosition: Int = position
        if (updatedPosition == mediaList.lastIndex) {
            updatedPosition = mediaList.lastIndex - 1 // because we will remove a media from the list
        }
        // Remove the media object from the list to save
        mediaList.remove(media)
        mediaAdapter?.notifyItemRemoved(position)
        mediaAdapter?.notifyDataSetChanged()
        // Update pager
        myUtils.addPagerToRecyclerView(requireActivity(), mediaList, updatedPosition, pager)
        validateMedia()
    }

    //----------------------------------------------------------------------------------
    // Methods to set error in TextInputLayout

    private fun validateType(): Boolean {
        return if (fragment_base_real_estate_type.editText?.text.toString() == "") {
            fragment_base_real_estate_type.error = getString(R.string.enter_type)
            false
        } else {
            fragment_base_real_estate_type.error = null
            true
        }
    }

    private fun validateMedia(): Boolean {
        return if (mediaList.size == 0) {
            recyclerView?.visibility = View.GONE
            fragment_base_real_estate_media_description.error = getString(R.string.enter_media)
            false
        } else {
            recyclerView?.visibility = View.VISIBLE
            fragment_base_real_estate_media_description.error = null
            true
        }
    }

    private fun validateSaleDate(): Boolean {
        return if (status == Status.SOLD.availability && saleDate.isNullOrEmpty()
                || status == Status.AVAILABLE.availability && !saleDate.isNullOrEmpty()
                || !saleDate.isNullOrEmpty() && status.isNullOrEmpty()) {
            fragment_base_real_estate_sale_date.error = getString(R.string.need_sale_date)
            false
        } else {
            fragment_base_real_estate_sale_date.error = null
            true
        }
    }

    //----------------------------------------------------------------------------------
    // Methods for medias

    @AfterPermissionGranted(RC_READ_PERM)
    private fun addPicture() {
        if (activity?.let { EasyPermissions.hasPermissions(it, *READ_PERM) }!!) {
            if (mediaDescription.isNullOrEmpty() || mediaDescription.isNullOrBlank()) {
                myUtils.showSnackbarMessage(requireActivity(), R.string.no_media_description)
            } else {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
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
            if (mediaDescription.isNullOrEmpty() || mediaDescription.isNullOrBlank()) {
                myUtils.showSnackbarMessage(requireActivity(), R.string.no_media_description)
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

    @AfterPermissionGranted(RC_WRITE_EXT_STORAGE_AND_CAMERA_PERMS)
    private fun takeVideo() {
        if (activity?.let { EasyPermissions.hasPermissions(it, *WRITE_EXT_STORAGE_AND_CAMERA_PERMS) }!!) {
            if (mediaDescription.isNullOrEmpty() || mediaDescription.isNullOrBlank()) {
                myUtils.showSnackbarMessage(requireActivity(), R.string.no_media_description)
            } else {
                Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
                    takeVideoIntent.resolveActivity(requireActivity().packageManager)?.also {
                        startActivityForResult(takeVideoIntent, RC_VIDEO_CAPTURE)
                    }
                }
            }
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_write_and_camera_permission_access),
                    RC_WRITE_EXT_STORAGE_AND_CAMERA_PERMS, *WRITE_EXT_STORAGE_AND_CAMERA_PERMS)
        }
    }

    private fun deleteMediaAlertDialog(media: Media, position: Int) {
        // Create an AlertDialog to request deletion of the media
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setMessage(R.string.delete_media)
        builder.apply {
            setPositiveButton(android.R.string.ok) { _, _ ->
                removeMediaFromListAndRefresh(media, position)
            }
            setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    //----------------------------------------------------------------------------------
    // Methods for data

    private fun impossibleToStoreLatLong() {
        // Create an AlertDialog to request deletion of the media
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setMessage(R.string.impossible_to_store_lat_long)
        builder.apply {
            setPositiveButton(android.R.string.ok) { _, _ ->
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun storeLatLng() {
        val streetNumberFormat = streetNumber ?: ""
        val streetFormat = street ?: ""
        val districtFormat = district ?: ""
        val postalCodeFormat = postalCode ?: ""
        val cityFormat = city ?: ""
        val countryFormat = country ?: ""

        val fullAddress = "$streetNumberFormat $streetFormat $districtFormat $postalCodeFormat $cityFormat $countryFormat"
        val addresses: List<Address>

        try {
            if (Utils.isInternetAvailable(activity)) {
                if (fullAddress.isNotBlank()) {
                    val geocoder = Geocoder(activity)
                    addresses = geocoder.getFromLocationName(fullAddress, 1)
                    if (addresses.isNotEmpty()) {
                        latitude = addresses[0].latitude
                        longitude = addresses[0].longitude
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        saveRealEstateIntent.putExtra(LATITUDE_REAL_ESTATE, latitude)
        saveRealEstateIntent.putExtra(LONGITUDE_REAL_ESTATE, longitude)
    }

    private fun saveRealEstateWithMedias() {
        if (type.isNullOrEmpty() || mediaList.isNullOrEmpty()) {
            myUtils.showSnackbarMessage(activity, R.string.add_type_and_media)
        } else if (status == Status.SOLD.availability && saleDate.isNullOrEmpty()
                || status == Status.AVAILABLE.availability && !saleDate.isNullOrEmpty()
                || !saleDate.isNullOrEmpty() && status.isNullOrEmpty()) {
            myUtils.showSnackbarMessage(activity, R.string.need_sale_date)
        } else {
            storeLatLng()
            saveRealEstateIntent.putExtra(ID_REAL_ESTATE, id)
            saveRealEstateIntent.putExtra(TYPE_REAL_ESTATE, type)
            saveRealEstateIntent.putExtra(PRICE_REAL_ESTATE, price)
            saveRealEstateIntent.putExtra(SURFACE_REAL_ESTATE, surface)
            saveRealEstateIntent.putExtra(ROOMS_REAL_ESTATE, roomsNumber)
            saveRealEstateIntent.putExtra(BATHROOMS_REAL_ESTATE, bathroomsNumber)
            saveRealEstateIntent.putExtra(BEDROOMS_REAL_ESTATE, bedroomsNumber)
            saveRealEstateIntent.putStringArrayListExtra(POI_REAL_ESTATE, pointsOfInterest)
            saveRealEstateIntent.putExtra(STREET_NUMBER_REAL_ESTATE, streetNumber)
            saveRealEstateIntent.putExtra(STREET_REAL_ESTATE, street)
            saveRealEstateIntent.putExtra(APARTMENT_NUMBER_REAL_ESTATE, apartmentNumber)
            saveRealEstateIntent.putExtra(DISTRICT_REAL_ESTATE, district)
            saveRealEstateIntent.putExtra(CITY_REAL_ESTATE, city)
            saveRealEstateIntent.putExtra(POSTAL_CODE_REAL_ESTATE, postalCode)
            saveRealEstateIntent.putExtra(COUNTRY_REAL_ESTATE, country)
            saveRealEstateIntent.putExtra(DESCRIPTION_REAL_ESTATE, description)
            saveRealEstateIntent.putExtra(STATUS_REAL_ESTATE, status)
            saveRealEstateIntent.putExtra(ENTRY_DATE_REAL_ESTATE, entryDate)
            saveRealEstateIntent.putExtra(SALE_DATE_REAL_ESTATE, saleDate)
            saveRealEstateIntent.putExtra(AGENT_REAL_ESTATE, agent)
            saveRealEstateIntent.putParcelableArrayListExtra(MEDIA_LIST_REAL_ESTATE, mediaList)

            activity?.setResult(Activity.RESULT_OK, saveRealEstateIntent)
            activity?.finish()
        }
    }

    //----------------------------------------------------------------------------------
    // Use DialogFragments data received in RealEstateActivity

    fun setPropertyChosen(propertyChosen: EditText?) {
        if (propertyChosen != null) {
            if (fragment_base_real_estate_type?.editText == propertyChosen) {
                type = propertyChosen.text.toString()
            } else {
                status = propertyChosen.text.toString()
            }
            validateSaleDate()
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
                fragment_base_real_estate_entry_date?.editText -> entryDate = editTextChosen?.text.toString()
                fragment_base_real_estate_sale_date?.editText -> saleDate = editTextChosen?.text.toString()
            }
        } else {
            when (editTextChosen) {
                fragment_base_real_estate_entry_date?.editText -> entryDate = null
                fragment_base_real_estate_sale_date?.editText -> saleDate = null
            }
        }

        // Parse String from EditText to Date then compare the two dates
        if (entryDate != null && entryDate != "" && saleDate != null && saleDate != "") {
            val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            dateOfEntry = sdf.parse(entryDate!!)
            dateOfSale = sdf.parse(saleDate!!)

            // The sale date cannot be earlier than the entry date
            if (dateOfSale!!.before(dateOfEntry)) {
                myUtils.showSnackbarMessage(requireActivity(), R.string.sale_date_earlier_entry_date)
                fragment_base_real_estate_entry_date.editText?.text?.clear()
                fragment_base_real_estate_sale_date.editText?.text?.clear()
                entryDate = null
                saleDate = null
            }
        }
        validateSaleDate()
    }

    //----------------------------------------------------------------------------------
    // Interfaces for callback from MediaAdapter

    override fun onClickMedia(position: Int) {
        // Do nothing
    }

    override fun onLongClickMedia(position: Int) {
        // Get the media object with the position in the RecyclerView
        val media: Media? = mediaAdapter?.getPosition(position)
        media?.let { deleteMediaAlertDialog(it, position) }
    }

    override fun onClickMediaFullScreen(position: Int) {
        // Do nothing
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
