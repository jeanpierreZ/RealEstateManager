package com.openclassrooms.realestatemanager.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.ItemActivity
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.adapters.ItemPicturesAdapter
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.utils.MyUtils
import com.openclassrooms.realestatemanager.views.viewmodels.ItemWithPicturesViewModel


/**
 * A simple [Fragment] subclass.
 */
class DetailsFragment : Fragment(),
        ItemPicturesAdapter.PictureListener,
        ItemPicturesAdapter.PictureLongClickListener,
        OnMapReadyCallback {

    companion object {
        private val TAG = DetailsFragment::class.java.simpleName

        // Key for itemWithPictures
        const val BUNDLE_ITEM_WITH_PICTURES: String = "BUNDLE_ITEM_WITH_PICTURES"

        // Keys for storing activity state
        private const val MAPVIEW_BUNDLE_KEY = "MAPVIEW_BUNDLE_KEY"
    }

    private var recyclerView: RecyclerView? = null
    private var itemPicturesAdapter: ItemPicturesAdapter? = null
    private var pictureList: ArrayList<Picture?> = arrayListOf()

    private var editIntent = Intent()

    private val myUtils = MyUtils()

    // Google Mobile Services Objects
    private var mapView: MapView? = null
    private lateinit var map: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val fragmentView: View = inflater.inflate(R.layout.fragment_details, container, false)

        val descriptionText: TextView = fragmentView.findViewById(R.id.details_fragment_description)
        val surfaceText: TextView = fragmentView.findViewById(R.id.details_fragment_surface)
        val roomText: TextView = fragmentView.findViewById(R.id.details_fragment_rooms)
        val bathroomText: TextView = fragmentView.findViewById(R.id.details_fragment_bathrooms)
        val bedroomText: TextView = fragmentView.findViewById(R.id.details_fragment_bedrooms)
        val streetText: TextView = fragmentView.findViewById(R.id.details_fragment_street)
        val apartmentText: TextView = fragmentView.findViewById(R.id.details_fragment_apartment)
        val cityText: TextView = fragmentView.findViewById(R.id.details_fragment_city)
        val postalCodeText: TextView = fragmentView.findViewById(R.id.details_fragment_postal_code)
        val countryText: TextView = fragmentView.findViewById(R.id.details_fragment_country)
        recyclerView = fragmentView.findViewById(R.id.details_fragment_recycler_view)
        mapView = fragmentView.findViewById(R.id.details_fragment_map)

        // For Toolbar menu
        setHasOptionsMenu(true)

        editIntent = Intent(activity, ItemActivity::class.java)

        // Get the identifier of the itemWithPictures from the bundle
        val itemWithPicturesId: Long? = arguments?.getLong(MainActivity.BUNDLE_ITEM_ID, -1)

        // Use the ViewModelProvider to associate the ViewModel with DetailsFragment
        val itemWithPicturesViewModel = ViewModelProvider(this).get(ItemWithPicturesViewModel::class.java)

        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK objects or sub-Bundles.
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mapView!!.onCreate(mapViewBundle)
        mapView!!.getMapAsync(this)
        mapView!!.isClickable = false

        // Observe changes in the itemWithPictures retrieved with the item id stored in the bundle
        itemWithPicturesViewModel.getModifiedItemWithPictures(itemWithPicturesId)
                .observe(viewLifecycleOwner, Observer { itemWithPictures ->

                    // Put the itemWithPictures in the intent, which is used when the user want to edit data
                    editIntent.putExtra(BUNDLE_ITEM_WITH_PICTURES, itemWithPictures)

                    // Get data to set addresses for textViews and map
                    val streetNumber = itemWithPictures?.item?.itemAddress?.streetNumber ?: ""
                    val street = itemWithPictures?.item?.itemAddress?.street ?: ""
                    val shortAddress = String.format("$streetNumber $street ")
                    val apartmentNumber = itemWithPictures?.item?.itemAddress?.apartmentNumber
                    val postalCode = itemWithPictures?.item?.itemAddress?.postalCode
                    val city = itemWithPictures?.item?.itemAddress?.city
                    val country = itemWithPictures?.item?.itemAddress?.country

                    // Get data to set Integers for textViews
                    val surface = itemWithPictures?.item?.surface
                    val roomsNumber = itemWithPictures?.item?.roomsNumber
                    val bathroomsNumber = itemWithPictures?.item?.bathroomsNumber
                    val bedroomsNumber = itemWithPictures?.item?.bedroomsNumber

                    // Set the editTexts
                    descriptionText.text = itemWithPictures?.item?.description

                    myUtils.displayIntegerProperties(surface, surfaceText)
                    myUtils.displayIntegerProperties(roomsNumber, roomText)
                    myUtils.displayIntegerProperties(bathroomsNumber, bathroomText)
                    myUtils.displayIntegerProperties(bedroomsNumber, bedroomText)

                    streetText.text = shortAddress
                    if (apartmentNumber.isNullOrBlank() || apartmentNumber == "") {
                        apartmentText.text = apartmentNumber
                    } else {
                        apartmentText.text = String.format(getString(R.string.apt) + " $apartmentNumber")
                    }
                    cityText.text = city
                    postalCodeText.text = postalCode
                    countryText.text = country

                    configureRecyclerView()
                    // Clear the pictureList in case of reuse it
                    pictureList.clear()
                    // Add photos of the chosen real estate from ListFragment
                    itemWithPictures?.pictures?.let { pictureList.addAll(it) }
                    updateUI(pictureList)
                    Log.d(TAG, "pictureList = $pictureList")

                    // Display the real estate on the map
                    clearMap()
                    val realEstateLatLng = itemWithPictures?.item?.itemAddress?.latitude?.let {
                        itemWithPictures.item.itemAddress.longitude?.let { it1 ->
                            LatLng(it, it1)
                        }
                    }
                    if (realEstateLatLng != null) {
                        if (realEstateLatLng == LatLng(0.0, 0.0) && fragmentView.isVisible) {
                            Snackbar.make(fragmentView, getString(R.string.address_not_available), Snackbar.LENGTH_LONG).show()
                        } else if (realEstateLatLng != LatLng(0.0, 0.0)) {
                            addRealEstateMarker(realEstateLatLng)
                        }
                    }
                })

        return fragmentView
    }

    //----------------------------------------------------------------------------------
    // Methods for Toolbar Menu

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.menu_toolbar_edit)?.isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_toolbar_edit) {
            editIntent.putExtra(MainActivity.TITLE_ITEM_ACTIVITY, getString(R.string.update_real_estate))
            activity?.startActivityForResult(editIntent, MainActivity.UPDATE_ITEM_ACTIVITY_REQUEST_CODE)
        }
        return super.onOptionsItemSelected(item)
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerViews, Adapters & LayoutManager

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of pictures
        itemPicturesAdapter = ItemPicturesAdapter(pictureList, Glide.with(this), this, this)
        // Attach the adapter to the recyclerView to populate pictures
        recyclerView?.adapter = itemPicturesAdapter
        // Set layout manager to position the pictures
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun updateUI(updateList: ArrayList<Picture?>) {
        itemPicturesAdapter?.setPictures(updateList)
    }

    //----------------------------------------------------------------------------------
    // Interfaces for callback from ItemPicturesAdapter

    override fun onClickPicture(position: Int) {
        Log.d("DETAILS", "CLICK on picture !")
    }

    override fun onLongClickItem(position: Int) {
        Log.d("DETAILS", "LONG CLICK on picture !")
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
