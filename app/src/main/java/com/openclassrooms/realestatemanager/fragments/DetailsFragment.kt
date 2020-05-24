package com.openclassrooms.realestatemanager.fragments


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.activities.RealEstateActivity
import com.openclassrooms.realestatemanager.adapters.MediaAdapter
import com.openclassrooms.realestatemanager.databinding.FragmentDetailsBinding
import com.openclassrooms.realestatemanager.models.Media
import com.openclassrooms.realestatemanager.utils.MyUtils
import com.openclassrooms.realestatemanager.views.viewmodels.RealEstateWithMediasViewModel


/**
 * A simple [Fragment] subclass.
 */
class DetailsFragment : Fragment(),
        MediaAdapter.MediaListener,
        MediaAdapter.MediaLongClickListener,
        OnMapReadyCallback {

    companion object {
        private val TAG = DetailsFragment::class.java.simpleName

        // Key for itemWithPictures
        const val BUNDLE_REAL_ESTATE_WITH_MEDIAS: String = "BUNDLE_REAL_ESTATE_WITH_MEDIAS"

        // Keys for storing activity state
        private const val MAPVIEW_BUNDLE_KEY = "MAPVIEW_BUNDLE_KEY"
    }

    private var mediaAdapter: MediaAdapter? = null

    private var mediaList: ArrayList<Media?> = arrayListOf()

    private var editRealEstateIntent = Intent()

    private val myUtils = MyUtils()

    // Google Mobile Services Objects
    private var mapView: MapView? = null
    private lateinit var map: GoogleMap

    // Declare callback
    private var callbackMedia: OnMediaClickedListener? = null

    // View binding
    private var _binding: FragmentDetailsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        mapView = binding.detailsFragmentMap

        // For Toolbar menu
        setHasOptionsMenu(true)

        editRealEstateIntent = Intent(activity, RealEstateActivity::class.java)

        // Get the identifier of the realEstateWithMedias from the bundle
        val realEstateWithMediasId: Long? = arguments?.getLong(MainActivity.REAL_ESTATE_ID_USED_FOR_DETAIL, -1)

        // Use the ViewModelProvider to associate the ViewModel with DetailsFragment
        val realEstateWithMediasViewModel = ViewModelProvider(this).get(RealEstateWithMediasViewModel::class.java)

        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK objects or sub-Bundles.
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mapView!!.onCreate(mapViewBundle)
        mapView!!.getMapAsync(this)
        mapView!!.isClickable = false

        // Observe changes in the realEstateWithMedias retrieved with the real estate id stored in the bundle
        realEstateWithMediasViewModel.getModifiedRealEstateWithMedias(realEstateWithMediasId)
                .observe(viewLifecycleOwner, Observer { realEstateWithMedias ->

                    // Put the realEstateWithMedias in the intent, which is used when the user want to edit data
                    editRealEstateIntent.putExtra(BUNDLE_REAL_ESTATE_WITH_MEDIAS, realEstateWithMedias)

                    // Get data to set addresses for textViews and map
                    val streetNumber = realEstateWithMedias?.realEstate?.address?.streetNumber ?: ""
                    val street = realEstateWithMedias?.realEstate?.address?.street ?: ""
                    val shortAddress = String.format("$streetNumber $street ")
                    val apartmentNumber = realEstateWithMedias?.realEstate?.address?.apartmentNumber
                    val postalCode = realEstateWithMedias?.realEstate?.address?.postalCode
                    val city = realEstateWithMedias?.realEstate?.address?.city
                    val country = realEstateWithMedias?.realEstate?.address?.country

                    // Get data to set Integers for textViews
                    val surface = realEstateWithMedias?.realEstate?.surface
                    val roomsNumber = realEstateWithMedias?.realEstate?.roomsNumber
                    val bathroomsNumber = realEstateWithMedias?.realEstate?.bathroomsNumber
                    val bedroomsNumber = realEstateWithMedias?.realEstate?.bedroomsNumber

                    with(binding) {
                        // Set the editTexts
                        detailsFragmentDescription.text = realEstateWithMedias?.realEstate?.description

                        myUtils.displayIntegerProperties(surface, detailsFragmentSurface)
                        myUtils.displayIntegerProperties(roomsNumber, detailsFragmentRooms)
                        myUtils.displayIntegerProperties(bathroomsNumber, detailsFragmentBathrooms)
                        myUtils.displayIntegerProperties(bedroomsNumber, detailsFragmentBedrooms)

                        detailsFragmentStreet.text = shortAddress
                        if (apartmentNumber.isNullOrBlank() || apartmentNumber == "") {
                            detailsFragmentApartment.text = apartmentNumber
                        } else {
                            detailsFragmentApartment.text = String.format(getString(R.string.apt) + " $apartmentNumber")
                        }
                        detailsFragmentCity.text = city
                        detailsFragmentPostalCode.text = postalCode
                        detailsFragmentCountry.text = country
                    }

                    configureRecyclerView()
                    // Clear the mediaList in case of reuse it
                    mediaList.clear()
                    // Add photos of the chosen real estate from ListFragment
                    realEstateWithMedias?.medias?.let { mediaList.addAll(it) }
                    updateUI(mediaList)
                    Log.d(TAG, "pictureList = $mediaList")

                    // Display the real estate on the map
                    clearMap()
                    val realEstateLatLng = realEstateWithMedias?.realEstate?.address?.latitude?.let {
                        realEstateWithMedias.realEstate.address?.longitude?.let { it1 ->
                            LatLng(it, it1)
                        }
                    }
                    if (realEstateLatLng != null) {
                        if (realEstateLatLng == LatLng(0.0, 0.0) && view.isVisible) {
                            Snackbar.make(view, getString(R.string.address_not_available), Snackbar.LENGTH_SHORT).show()
                        } else if (realEstateLatLng != LatLng(0.0, 0.0)) {
                            addRealEstateMarker(realEstateLatLng)
                        }
                    }
                })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //----------------------------------------------------------------------------------
    // Methods for Toolbar Menu

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.menu_toolbar_edit)?.isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_toolbar_edit) {
            editRealEstateIntent.putExtra(MainActivity.TITLE_REAL_ESTATE_ACTIVITY, getString(R.string.update_real_estate))
            activity?.startActivityForResult(editRealEstateIntent, MainActivity.UPDATE_REAL_ESTATE_ACTIVITY_REQUEST_CODE)
        }
        return super.onOptionsItemSelected(item)
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerViews, Adapters, LayoutManager & UI

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of media
        mediaAdapter = MediaAdapter(mediaList, Glide.with(this), this, this)
        // Attach the adapter to the recyclerView to populate medias
        binding.detailsFragmentRecyclerView.adapter = mediaAdapter
        // Set layout manager to position the medias
        binding.detailsFragmentRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun updateUI(updateList: ArrayList<Media?>) {
        mediaAdapter?.setMedias(updateList)
    }

    //----------------------------------------------------------------------------------
    // Interfaces for callback from MediaAdapter

    override fun onClickMedia(position: Int) {
        // Get media from position
        val media = mediaList[position]
        var isMediaVideo = true

        var mediaToPlay: Uri? = Uri.EMPTY
        if (media?.mediaPicture?.isAbsolute!!) {
            mediaToPlay = mediaList[position]?.mediaPicture
            isMediaVideo = false
        } else if (media.mediaVideo?.isAbsolute!!) {
            mediaToPlay = mediaList[position]?.mediaVideo
        }

        if (mediaToPlay != Uri.EMPTY) {
            Log.d(TAG, "Click on $mediaToPlay which isMediaVideo = $isMediaVideo")
            callbackMedia?.onMediaClicked(mediaToPlay, isMediaVideo)
        }
    }

    override fun onLongClickMedia(position: Int) {
        Log.d(TAG, "LONG CLICK on media !")
    }

    //----------------------------------------------------------------------------------

    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap
        // Prevent to show the My Location button, the MapToolbar and the Compass
        map.uiSettings.isMyLocationButtonEnabled = false
        map.uiSettings.isMapToolbarEnabled = false
        map.uiSettings.isCompassEnabled = false
    }

    //----------------------------------------------------------------------------------
    // For Google Map

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView?.onSaveInstanceState(mapViewBundle)
    }

    // Clear the map
    private fun clearMap() {
        // Clear the map and display Null Island, an imaginary island located in the Gulf of Guinea.
        // Its particularity is that it has zero geographic coordinates (0, 0).
        val latLng = LatLng(0.0, 0.0)
        map.clear()
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    // Display the real estate on the map
    private fun addRealEstateMarker(latLng: LatLng) {
        map.addMarker(MarkerOptions().position(latLng))
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    //----------------------------------------------------------------------------------
    // Interface for callback to parent activity and associated methods when click on a media

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the method that creating callback after being attached to parent activity
        createCallbackToParentActivity()
    }

    // Declare our interface that will be implemented by any container activity
    interface OnMediaClickedListener {
        fun onMediaClicked(mediaVideo: Uri?, isMediaVideo: Boolean)
    }

    // Create callback to parent activity
    private fun createCallbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            callbackMedia = activity as OnMediaClickedListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement OnMediaClickedListener")
        }
    }

}
