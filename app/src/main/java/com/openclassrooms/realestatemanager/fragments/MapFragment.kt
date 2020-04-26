package com.openclassrooms.realestatemanager.fragments

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.ItemWithPictures
import com.openclassrooms.realestatemanager.models.Status
import com.openclassrooms.realestatemanager.views.viewmodels.ItemWithPicturesViewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment(), OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    companion object {
        private val TAG = MapFragment::class.java.simpleName

        // Keys for storing activity state
        private const val MAPVIEW_BUNDLE_KEY = "MAPVIEW_BUNDLE_KEY"
        private const val KEY_LOCATION = "KEY_LOCATION"
        private const val KEY_CAMERA_POSITION = "KEY_CAMERA_POSITION"

        // The default zoom for the map
        private const val DEFAULT_ZOOM = 17

        // Key for Location Permissions
        val LOCATION_PERMS = arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)

        // Request code for location permission
        const val RC_LOCATION_PERMS = 222
    }

    // Declare callback
    private var callbackMarker: OnMarkerClickedListener? = null

    // Google Mobile Services Objects
    private var mMapView: MapView? = null

    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null

    // Location
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation: Location? = null

    // A default location (New York, USA) to use when location permission is not granted.
    private val defaultLocation = LatLng(40.7127281, -74.0060152)

    private lateinit var itemWithPicturesViewModel: ItemWithPicturesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_map, container, false)

        mMapView = fragmentView.findViewById(R.id.fragment_map_view)

        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK objects or sub-Bundles.
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }

        mMapView!!.onCreate(mapViewBundle)
        mMapView!!.getMapAsync(this)

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }

        // Construct a FusedLocationProviderClient
        fusedLocationProviderClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }

        return fragmentView
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap

        // Prevent to show the the MapToolbar
        map?.uiSettings?.isMapToolbarEnabled = false

        // If permissions are granted...
        getDeviceLocation()
        getDataAndShowRealEstates()

        map?.setOnMarkerClickListener { marker: Marker ->
            // Retrieve the data from the marker
            val itemWithPicturesId = marker.tag as Long?
            // Spread the click to the parent activity with the item id
            callbackMarker?.onMarkerClicked(itemWithPicturesId)
            false
        }
    }

    //----------------------------------------------------------------------------------
    // LifeCycle of Map
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mMapView?.onSaveInstanceState(mapViewBundle)

        // Save the state of the map when the activity is paused
        if (map != null) {
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
            outState.putParcelable(KEY_CAMERA_POSITION, map?.cameraPosition)
            super.onSaveInstanceState(outState)
        }
    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mMapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView?.onStop()
    }

    override fun onPause() {
        mMapView?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mMapView?.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView?.onLowMemory()
    }

    //----------------------------------------------------------------------------------
    // Method for device location
    @AfterPermissionGranted(RC_LOCATION_PERMS)
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (activity?.let { EasyPermissions.hasPermissions(it, *LOCATION_PERMS) }!!) {

                val locationResult = fusedLocationProviderClient?.lastLocation

                activity?.let {
                    locationResult?.addOnCompleteListener(it) { task: Task<Location?> ->
                        if (task.isSuccessful && task.result != null) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.result
                            val currentLocation = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
                            Log.i(TAG, "Current location = $currentLocation")

                            // Construct a CameraPosition focusing on the current location...
                            // ...and animate the camera to that position.
                            cameraPosition = CameraPosition.Builder()
                                    .target(currentLocation)
                                    .zoom(DEFAULT_ZOOM.toFloat())
                                    .build()
                            map?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                        } else {
                            Log.i(TAG, "Current location is null. Using defaults.")
                            Log.e(TAG, "Exception: %s", task.exception)
                            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                        }
                    }
                }
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.rationale_location_permission_access),
                        RC_LOCATION_PERMS, *LOCATION_PERMS)
            }
        } catch (e: SecurityException) {
            e.message?.let {
                Log.e("Exception: %s", it)
            }
        }
    }

    //----------------------------------------------------------------------------------
    // Methods to build and show markers on Map

    private fun getDataAndShowRealEstates() {
        // Add markers at real estates location
        map?.clear() // clear older markers
        itemWithPicturesViewModel = ViewModelProvider(this).get(ItemWithPicturesViewModel::class.java)
        itemWithPicturesViewModel.getItemWithPictures.observe(this, Observer { itemWithPictures ->
            for (itemWithPicture in itemWithPictures) {
                itemWithPicture?.let { showRealEstates(it) }
            }
        })
    }


    // Display markers at real estates location
    private fun showRealEstates(itemWithPictures: ItemWithPictures) {
        if (activity?.let { EasyPermissions.hasPermissions(it, *LOCATION_PERMS) }!!) {
            val latLng = itemWithPictures.item.itemAddress?.latitude?.let {
                itemWithPictures.item.itemAddress.longitude?.let { it1 -> LatLng(it, it1) }
            }

            if (latLng != null) {
                // If the real estate is sold, mark a red icon
                if (itemWithPictures.item.status == Status.SOLD.availability) {
                    addMarkers(latLng, R.drawable.ic_location_on_red_24dp, itemWithPictures.item.id)
                } else {
                    // If the real estate is available or has a null status, mark a green icon
                    addMarkers(latLng, R.drawable.ic_location_on_green_24dp, itemWithPictures.item.id)
                }
            }
        }
    }


    // Add different color markers depending on whether real estates are available or sold
    private fun addMarkers(latLng: LatLng, icLocation: Int, itemWithPicturesId: Long?) {
        if (latLng != LatLng(0.0, 0.0)) {
            val marker: Marker? = map?.addMarker(MarkerOptions()
                    .icon(bitmapDescriptorFromVector(icLocation))
                    .position(latLng))
            marker?.tag = itemWithPicturesId
        }
    }

    // Create icon for the map
    private fun bitmapDescriptorFromVector(resId: Int): BitmapDescriptor? {
        // Create drawable
        val drawable: Drawable? = VectorDrawableCompat.create(resources, resId, null)
        if (drawable == null) {
            Log.e(TAG, "Requested vector resource was not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        drawable.setBounds(0, 0,
                drawable.intrinsicWidth, drawable.intrinsicHeight)

        // Create Bitmap with drawable then draw it
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
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
        getDeviceLocation()
        getDataAndShowRealEstates()
    }

    //----------------------------------------------------------------------------------
    // Interface for callback to parent activity and associated methods when click on a marker

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the method that creating callback after being attached to parent activity
        createCallbackToParentActivity()
    }

    // Declare our interface that will be implemented by any container activity
    interface OnMarkerClickedListener {
        fun onMarkerClicked(itemWithPicturesId: Long?)
    }

    // Create callback to parent activity
    private fun createCallbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            callbackMarker = activity as OnMarkerClickedListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement OnMarkerClickedListener")
        }
    }

}