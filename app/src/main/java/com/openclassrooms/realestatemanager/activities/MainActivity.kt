package com.openclassrooms.realestatemanager.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.fragments.BaseItemFragment
import com.openclassrooms.realestatemanager.fragments.DetailsFragment
import com.openclassrooms.realestatemanager.fragments.ListFragment
import com.openclassrooms.realestatemanager.fragments.MapFragment
import com.openclassrooms.realestatemanager.models.Item
import com.openclassrooms.realestatemanager.models.ItemAddress
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.utils.MyUtils
import com.openclassrooms.realestatemanager.views.viewmodels.ItemWithPicturesViewModel
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : AppCompatActivity(),
        ListFragment.OnItemClickedListener,
        MapFragment.OnMarkerClickedListener,
        EasyPermissions.PermissionCallbacks,
        NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        // Key for item id
        const val BUNDLE_ITEM_ID: String = "BUNDLE_ITEM_ID"

        // Key for item title
        const val TITLE_ITEM_ACTIVITY: String = "TITLE_ITEM_ACTIVITY"

        // Static data for Permissions
        val PERMS = arrayOf(Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)

        // Request codes
        const val PERMS_REQUEST_CODE = 111
        const val ITEM_ACTIVITY_REQUEST_CODE = 1
        const val UPDATE_ITEM_ACTIVITY_REQUEST_CODE = 2
    }

    private lateinit var itemWithPicturesViewModel: ItemWithPicturesViewModel

    private val detailsFragment = DetailsFragment()
    private val listFragment = ListFragment()
    private val mapFragment = MapFragment()

    private val myUtils = MyUtils()

    // For design
    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val drawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureToolbar()
        configureDrawerLayout()
        configureNavigationView()

        // Request permissions when starting MainActivity
        EasyPermissions.requestPermissions(this, getString(R.string.rationale_permission_access),
                PERMS_REQUEST_CODE, *PERMS)

        getRealEstates()
        displayListFragment()
        displayDetailsFragmentAtLaunchInTabletMode()
    }

    //----------------------------------------------------------------------------------

    private fun getRealEstates() {
        // Display the list of real estates if permissions were already allowed
        if (EasyPermissions.hasPermissions(this, *PERMS)) {
            // Use the ViewModelProvider to associate the ViewModel with MainActivity
            itemWithPicturesViewModel = ViewModelProvider(this).get(ItemWithPicturesViewModel::class.java)
        }
    }

    //----------------------------------------------------------------------------------

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                ITEM_ACTIVITY_REQUEST_CODE -> {
                    // Insert itemWithPictures in database
                    itemWithPicturesViewModel
                            .insertItemWithPictures(itemData(data, null), pictureData(data))
                }

                UPDATE_ITEM_ACTIVITY_REQUEST_CODE -> {
                    // Update itemWithPictures in database
                    itemWithPicturesViewModel
                            .updateItemWithPictures(itemData(data,
                                    data?.getLongExtra(BaseItemFragment.ID_ITEM, 0)),
                                    pictureData(data))
                }
            }
        } else {
            myUtils.showMessageRealEstateNotSaved(applicationContext)
        }
    }

    //----------------------------------------------------------------------------------
    // Methods to set data from onActivityResult

    private fun itemData(data: Intent?, id: Long?): Item {
        // Create an address with data from ItemActivity
        val itemAddress = ItemAddress(data?.getStringExtra(BaseItemFragment.STREET_NUMBER_ITEM),
                data?.getStringExtra(BaseItemFragment.STREET_ITEM),
                data?.getStringExtra(BaseItemFragment.APARTMENT_NUMBER_ITEM),
                data?.getStringExtra(BaseItemFragment.DISTRICT_ITEM),
                data?.getStringExtra(BaseItemFragment.CITY_ITEM),
                data?.getStringExtra(BaseItemFragment.POSTAL_CODE_ITEM),
                data?.getStringExtra(BaseItemFragment.COUNTRY_ITEM),
                data?.getDoubleExtra(BaseItemFragment.LATITUDE_ITEM, 0.0),
                data?.getDoubleExtra(BaseItemFragment.LONGITUDE_ITEM, 0.0))

        // Create an item with data from ItemActivity
        return Item(id, data?.getStringExtra(BaseItemFragment.TYPE_ITEM),
                data?.getIntExtra(BaseItemFragment.PRICE_ITEM, -1),
                data?.getIntExtra(BaseItemFragment.SURFACE_ITEM, -1),
                data?.getIntExtra(BaseItemFragment.ROOMS_ITEM, -1),
                data?.getIntExtra(BaseItemFragment.BATHROOMS_ITEM, -1),
                data?.getIntExtra(BaseItemFragment.BEDROOMS_ITEM, -1),
                data?.getStringArrayListExtra(BaseItemFragment.POI_ITEM),
                itemAddress,
                data?.getStringExtra(BaseItemFragment.DESCRIPTION_ITEM),
                data?.getStringExtra(BaseItemFragment.STATUS_ITEM),
                data?.getStringExtra(BaseItemFragment.ENTRY_DATE_ITEM),
                data?.getStringExtra(BaseItemFragment.SALE_DATE_ITEM),
                data?.getStringExtra(BaseItemFragment.AGENT_ITEM))
    }

    private fun pictureData(data: Intent?): ArrayList<Picture?> {
        // Create a list of pictures with data from ItemActivity
        return data?.getParcelableArrayListExtra<Picture>(BaseItemFragment.PICTURE_LIST_ITEM)
                as ArrayList<Picture?>
    }

    //----------------------------------------------------------------------------------
    // Methods for Toolbar Menu

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        menu?.findItem(R.id.menu_toolbar_edit)?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_toolbar_add -> {
                val intent = Intent(this, ItemActivity::class.java)
                intent.putExtra(TITLE_ITEM_ACTIVITY, getString(R.string.create_real_estate))
                startActivityForResult(intent, ITEM_ACTIVITY_REQUEST_CODE)
            }
            R.id.menu_toolbar_search -> {
                Toast.makeText(this, "search", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //----------------------------------------------------------------------------------
    // Private methods to configure design

    private fun configureToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun configureDrawerLayout() {
        // "Hamburger icon"
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, android.R.color.white)
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun configureNavigationView() {
        val navigationView: NavigationView = findViewById(R.id.activity_main_nav_view)
        // For Menu Item
        navigationView.setNavigationItemSelectedListener(this)
    }

    //----------------------------------------------------------------------------------
    // Methods for NavigationView in NavigationDrawer

    override fun onBackPressed() {
        // Handle back click to close menu
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle Navigation Item Click
        when (item.itemId) {

            R.id.menu_nav_drawer_main_activity -> {
                // Check if we are in Tablet mode and adapt UI
                if (findViewById<View>(R.id.activity_main_map_fragment_container_view) != null) {
                    hideMapFragment()
                    displayDetailsFragmentAtLaunchInTabletMode()
                }
                getRealEstates()
                displayListFragment()
            }

            R.id.menu_nav_drawer_map_fragment -> {
                // In Tablet mode, if mapFragment is already called, we adapt UI
                if (mapFragment.isHidden) {
                    hideListAndDetailsFragment()
                    refreshMapFragment()
                } else {
                    displayMapFragment()
                }
            }
        }
        this.drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    //----------------------------------------------------------------------------------
    // Private methods to display fragments

    private fun displayListFragment() {
        // In Tablet mode, don't addToBackStack
        if (findViewById<View>(R.id.activity_main_details_fragment_container_view) != null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_main_fragment_container_view, listFragment)
                    .attach(listFragment)
                    .show(listFragment)
                    .commit()
        } else {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_main_fragment_container_view, listFragment)
                    .addToBackStack(listFragment.toString())
                    .commit()
        }
    }

    private fun displayDetailsFragmentAtLaunchInTabletMode() {
        // At launch, only add DetailsFragment in Tablet mode, but hide the fragment because no real estate is selected
        if (findViewById<View>(R.id.activity_main_details_fragment_container_view) != null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_main_details_fragment_container_view, detailsFragment)
                    .hide(detailsFragment)
                    .commit()
        }
    }

    private fun displayDetailsFragment() {
        // Refresh detailsFragment in Tablet mode
        if (findViewById<View>(R.id.activity_main_details_fragment_container_view) != null) {
            supportFragmentManager.beginTransaction().detach(detailsFragment).commit()
            // show() is used because detailsFragment is hide at launch
            supportFragmentManager.beginTransaction().attach(detailsFragment).show(detailsFragment)
                    .addToBackStack(detailsFragment.toString())
                    .commit()
        } else {
            // Display detailsFragment instead of ListFragment in Phone mode
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_main_fragment_container_view, detailsFragment)
                    .addToBackStack(detailsFragment.toString())
                    .commit()
        }
    }

    private fun displayMapFragment() {
        //  In Tablet mode, display mapFragment at the 1st call
        if (findViewById<View>(R.id.activity_main_map_fragment_container_view) != null) {
            // Hide listFragment and detailsFragment
            hideListAndDetailsFragment()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_main_map_fragment_container_view, mapFragment)
                    .addToBackStack(mapFragment.toString())
                    .commit()
        } else {
            // Display mapFragment instead of ListFragment or DetailsFragment in Phone mode
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_main_fragment_container_view, mapFragment)
                    .addToBackStack(mapFragment.toString())
                    .commit()
        }
    }

    private fun hideListAndDetailsFragment() {
        supportFragmentManager.beginTransaction().hide(listFragment).commit()
        supportFragmentManager.beginTransaction().hide(detailsFragment).commit()
    }

    private fun hideMapFragment() {
        supportFragmentManager.beginTransaction().hide(mapFragment).commit()
    }

    private fun refreshMapFragment() {
        supportFragmentManager.beginTransaction().detach(mapFragment).commit()
        // show() is used because mapFragment is hide when Real Estate is called in NavigationDrawer
        supportFragmentManager.beginTransaction().attach(mapFragment).show(mapFragment)
                .addToBackStack(mapFragment.toString())
                .commit()
    }

    //----------------------------------------------------------------------------------
    // Put the item id in a bundle and fetch it in detailsFragment

    private fun putIdInBundle(itemWithPicturesId: Long?) {
        val bundleItem = Bundle()
        itemWithPicturesId?.let { bundleItem.putLong(BUNDLE_ITEM_ID, it) }
        detailsFragment.arguments = bundleItem
    }

    //----------------------------------------------------------------------------------

    // Implement listener from ListFragment to display DetailsFragment when click on an item
    override fun onItemClicked(itemWithPicturesId: Long?) {
        putIdInBundle(itemWithPicturesId)
        displayDetailsFragment()
    }

    // Implement listener from MapFragment to display DetailsFragment when click on a marker
    override fun onMarkerClicked(itemWithPicturesId: Long?) {
        // Check if we are in Tablet mode and adapt UI
        if (findViewById<View>(R.id.activity_main_map_fragment_container_view) != null) {
            hideMapFragment()
            displayListFragment()
        }
        putIdInBundle(itemWithPicturesId)
        displayDetailsFragment()
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
        itemWithPicturesViewModel = ViewModelProvider(this).get(ItemWithPicturesViewModel::class.java)
        displayListFragment()
    }

}
