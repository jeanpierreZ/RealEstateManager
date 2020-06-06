package com.openclassrooms.realestatemanager.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.activities.RealEstateActivity
import com.openclassrooms.realestatemanager.adapters.MediaAdapter
import com.openclassrooms.realestatemanager.models.Media
import com.openclassrooms.realestatemanager.utils.MyUtils
import com.openclassrooms.realestatemanager.views.viewmodels.RealEstateWithMediasViewModel
import kotlinx.android.synthetic.main.fragment_details.*


/**
 * A simple [Fragment] subclass.
 */
class DetailsFragment : Fragment(),
        MediaAdapter.MediaListener,
        MediaAdapter.MediaLongClickListener,
        MediaAdapter.MediaFullScreenListener,
        OnMapReadyCallback {

    companion object {
        private val TAG = DetailsFragment::class.java.simpleName

        // Key for intent to edit a realEstateWithMedias in RealEstateActivity
        const val REAL_ESTATE_WITH_MEDIAS: String = "REAL_ESTATE_WITH_MEDIAS"

        // Bundles for PlayerFragment
        const val BUNDLE_MEDIA_LIST: String = "BUNDLE_MEDIA_LIST"
        const val BUNDLE_MEDIA_POSITION: String = "BUNDLE_MEDIA_POSITION"

        // Keys for storing activity state
        private const val MAPVIEW_BUNDLE_KEY = "MAPVIEW_BUNDLE_KEY"
    }

    private var recyclerView: RecyclerView? = null
    private var mediaAdapter: MediaAdapter? = null
    private val isFullScreen = false
    private val isRealEstateActivity = false

    private var mediaList: ArrayList<Media?> = arrayListOf()

    private var editRealEstateIntent = Intent()

    private val myUtils = MyUtils()

    // Google Mobile Services Objects
    private var mapView: MapView? = null
    private lateinit var map: GoogleMap

    // Nested PlayerFragment
    private val playerFragment: Fragment = PlayerFragment()

    private lateinit var fragmentTransaction: FragmentTransaction

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView: View = inflater.inflate(R.layout.fragment_details, container, false)

        recyclerView = fragmentView.findViewById(R.id.details_fragment_recycler_view)
        mapView = fragmentView.findViewById(R.id.details_fragment_map)

        // For Toolbar menu
        setHasOptionsMenu(true)

        // This callback will only be called when DetailsFragment is at least Started.
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            Log.d(TAG, "DetailsFragment callback back pressed")
            // Handle the back button event...
            if (playerFragment.isAdded) {
                // ... to remove nested PlayerFragment and return to parent DetailsFragment...
                removeNestedPlayerFragment()
            } else {
                // ... or return to backstack
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        // Enabled the callback
        callback.isEnabled = true

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
                    editRealEstateIntent.putExtra(REAL_ESTATE_WITH_MEDIAS, realEstateWithMedias)

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

                    // Set the editTexts
                    details_fragment_description.text = realEstateWithMedias?.realEstate?.description

                    myUtils.displayIntegerProperties(surface, details_fragment_surface)
                    myUtils.displayIntegerProperties(roomsNumber, details_fragment_rooms)
                    myUtils.displayIntegerProperties(bathroomsNumber, details_fragment_bathrooms)
                    myUtils.displayIntegerProperties(bedroomsNumber, details_fragment_bedrooms)

                    details_fragment_street.text = shortAddress
                    if (apartmentNumber.isNullOrBlank() || apartmentNumber == "") {
                        details_fragment_apartment.text = apartmentNumber
                    } else {
                        details_fragment_apartment.text = String.format(getString(R.string.apt) + " $apartmentNumber")
                    }
                    details_fragment_city.text = city
                    details_fragment_postal_code.text = postalCode
                    details_fragment_country.text = country

                    configureRecyclerView()
                    // Clear the mediaList in case of reuse it
                    mediaList.clear()
                    // Add medias of the chosen real estate from ListFragment
                    realEstateWithMedias?.medias?.let { mediaList.addAll(it) }
                    updateMediaList(mediaList)
                    Log.d(TAG, "mediaList = $mediaList")

                    // Display the real estate on the map
                    clearMap()
                    val realEstateLatLng = realEstateWithMedias?.realEstate?.address?.latitude?.let {
                        realEstateWithMedias.realEstate.address?.longitude?.let { it1 ->
                            LatLng(it, it1)
                        }
                    }
                    if (realEstateLatLng != null) {
                        if (realEstateLatLng == LatLng(0.0, 0.0) && fragmentView.isVisible) {
                            myUtils.showSnackbarMessage(requireActivity(), getString(R.string.address_not_available))
                        } else if (realEstateLatLng != LatLng(0.0, 0.0)) {
                            addRealEstateMarker(realEstateLatLng)
                        }
                    }
                })

        return fragmentView
    }

    override fun onResume() {
        super.onResume()
        if (playerFragment.isAdded) {
            // Remove nested PlayerFragment and return to parent DetailsFragment
            removeNestedPlayerFragment()
        }
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
        mediaAdapter = activity?.let {
            MediaAdapter(mediaList, Glide.with(this), this, this, this, isFullScreen, isRealEstateActivity, it)
        }
        // Attach the adapter to the recyclerView to populate medias
        recyclerView?.adapter = mediaAdapter
        // Set layout manager to position the medias
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        // To swipe page after page
        recyclerView?.onFlingListener = null
        PagerSnapHelper().attachToRecyclerView(recyclerView)

        // Update the pagerRecyclerView when switch on an other page
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val position = (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
                myUtils.addPagerToRecyclerView(requireActivity(), mediaList, position, detailsFragmentPager)
            }
        })
    }

    private fun updateMediaList(updateList: ArrayList<Media?>) {
        mediaAdapter?.setMedias(updateList)
        myUtils.addPagerToRecyclerView(requireActivity(), updateList, 0, detailsFragmentPager)
    }

    //----------------------------------------------------------------------------------
    // Interfaces for callback from MediaAdapter

    override fun onClickMedia(position: Int) {
        val bundle = Bundle()
        bundle.putParcelableArrayList(BUNDLE_MEDIA_LIST, mediaList)
        bundle.putInt(BUNDLE_MEDIA_POSITION, position)
        playerFragment.arguments = bundle
        addNestedPlayerFragment()
    }

    override fun onLongClickMedia(position: Int) {
        // Do nothing
    }

    override fun onClickMediaFullScreen(position: Int) {
        // Do nothing
    }

    //----------------------------------------------------------------------------------
    // Private methods to display child fragment

    private fun addNestedPlayerFragment() {
        fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.detailsFragmentLayout, playerFragment)
        fragmentTransaction.commit()
    }

    private fun removeNestedPlayerFragment() {
        fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.remove(playerFragment)
        fragmentTransaction.commit()
    }

    //----------------------------------------------------------------------------------
    // For Google Map

    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap
        // Prevent to show the My Location button, the MapToolbar and the Compass
        map.uiSettings.isMyLocationButtonEnabled = false
        map.uiSettings.isMapToolbarEnabled = false
        map.uiSettings.isCompassEnabled = false
    }

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

}
