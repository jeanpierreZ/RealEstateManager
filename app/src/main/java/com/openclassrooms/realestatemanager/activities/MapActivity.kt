package com.openclassrooms.realestatemanager.activities

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.openclassrooms.realestatemanager.R
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class MapActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, OnMapReadyCallback {

    companion object {
        private val TAG = MapActivity::class.java.simpleName

        // Keys for storing activity state
        private const val MAPVIEW_BUNDLE_KEY = "MAPVIEW_BUNDLE_KEY"
        private const val KEY_LOCATION = "KEY_LOCATION"
        private const val KEY_CAMERA_POSITION = "KEY_CAMERA_POSITION"

        // The default zoom for the map
        private const val DEFAULT_ZOOM = 17

        // Key for Location Permissions
        val LOCATION_PERMS = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

        // Request code fir location permission
        const val LOCATION_PERMS_REQUEST_CODE = 222
    }

    // Google Mobile Services Objects
    private val mMapView by lazy<MapView> { findViewById(R.id.activity_map_view) }
    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null

    // Location
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation: Location? = null

    // A default location (New York, USA) to use when location permission is not granted.
    private val defaultLocation = LatLng(40.7127281, -74.0060152)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK objects or sub-Bundles.
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }

        mMapView.onCreate(mapViewBundle)
        mMapView.getMapAsync(this)

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }

        // Construct a FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap

        // If permissions are granted, turn on My Location and the related control on the map
        updateLocationUI()
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
        mMapView.onSaveInstanceState(mapViewBundle)

        // Saves the state of the map when the activity is paused
        if (map != null) {
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
            outState.putParcelable(KEY_CAMERA_POSITION, map?.cameraPosition)
            super.onSaveInstanceState(outState)
        }
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView.onStop()
    }

    override fun onPause() {
        mMapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mMapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    //----------------------------------------------------------------------------------
    // Methods for location and configure Map
    @AfterPermissionGranted(LOCATION_PERMS_REQUEST_CODE)
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (EasyPermissions.hasPermissions(this, *LOCATION_PERMS)) {
                val locationResult = fusedLocationProviderClient?.lastLocation
                locationResult?.addOnCompleteListener(this) { task: Task<Location?> ->
                    if (task.isSuccessful && task.result != null) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        val currentLocation = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)

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
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.rationale_location_permission_access),
                        LOCATION_PERMS_REQUEST_CODE, *LOCATION_PERMS)
            }
        } catch (e: SecurityException) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            Log.e("Exception: %s", e.message)
        }
    }

    @AfterPermissionGranted(LOCATION_PERMS_REQUEST_CODE)
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (EasyPermissions.hasPermissions(this, *LOCATION_PERMS)) {
                // Go to My Location and give the related control on the map
                map?.isMyLocationEnabled = true
                getDeviceLocation()
            } else {
                map?.isMyLocationEnabled = false
                map = null
                EasyPermissions.requestPermissions(this, getString(R.string.rationale_location_permission_access),
                        LOCATION_PERMS_REQUEST_CODE, *LOCATION_PERMS)
            }
        } catch (e: SecurityException) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            Log.e("Exception: %s", e.message)
        }
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
        updateLocationUI()
    }

}
