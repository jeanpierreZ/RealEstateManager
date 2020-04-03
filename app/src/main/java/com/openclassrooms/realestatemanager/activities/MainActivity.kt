package com.openclassrooms.realestatemanager.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.openclassrooms.realestatemanager.models.Address
import com.openclassrooms.realestatemanager.models.Item
import com.openclassrooms.realestatemanager.models.ItemWithPictures
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.views.viewmodels.ItemWithPicturesViewModel
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : AppCompatActivity(), ListFragment.OnItemClickedListener, EasyPermissions.PermissionCallbacks,
        NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        // Key for item position
        const val BUNDLE_ITEM_WITH_PICTURES: String = "BUNDLE_ITEM_WITH_PICTURES"

        // Key for item title
        const val TITLE_ITEM_ACTIVITY: String = "TITLE_ITEM_ACTIVITY"

        // Static data for Permissions
        val PERMS = arrayOf(Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)

        // Request codes
        const val PERMS_REQUEST_CODE = 123
        const val ITEM_ACTIVITY_REQUEST_CODE = 1
    }

    private lateinit var itemWithPicturesViewModel: ItemWithPicturesViewModel

    private var detailsFragment = DetailsFragment()

    // For design
    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val drawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureToolbar()
        configureDrawerLayout()
        configureNavigationView()

        // Request permission when starting MainActivity
        EasyPermissions.requestPermissions(this, getString(R.string.rationale_permission_access),
                PERMS_REQUEST_CODE, *PERMS)

        // Open the view with RestaurantMapFragment if permissions were already allowed
        if (EasyPermissions.hasPermissions(this, *PERMS)) {
            // Use the ViewModelProvider to associate the ViewModel with MainActivity
            itemWithPicturesViewModel = ViewModelProvider(this).get(ItemWithPicturesViewModel::class.java)
            displayListFragment()
        }

        displayDetailsFragment()
    }

    //----------------------------------------------------------------------------------

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ITEM_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            // Create an address with data from ItemActivity
            val address = Address(data?.getStringExtra(BaseItemFragment.STREET_NUMBER_ITEM),
                    data?.getStringExtra(BaseItemFragment.STREET_ITEM),
                    data?.getStringExtra(BaseItemFragment.APARTMENT_NUMBER_ITEM),
                    data?.getStringExtra(BaseItemFragment.DISTRICT_ITEM),
                    data?.getStringExtra(BaseItemFragment.CITY_ITEM),
                    data?.getStringExtra(BaseItemFragment.POSTAL_CODE_ITEM),
                    data?.getStringExtra(BaseItemFragment.COUNTRY_ITEM))

            // Create an item with data from ItemActivity
            val item = Item(null, data?.getStringExtra(BaseItemFragment.TYPE_ITEM),
                    data?.getIntExtra(BaseItemFragment.PRICE_ITEM, 0),
                    data?.getIntExtra(BaseItemFragment.SURFACE_ITEM, 0),
                    data?.getIntExtra(BaseItemFragment.ROOMS_ITEM, 0),
                    data?.getIntExtra(BaseItemFragment.BATHROOMS_ITEM, 0),
                    data?.getIntExtra(BaseItemFragment.BEDROOMS_ITEM, 0),
                    data?.getStringArrayListExtra(BaseItemFragment.POI_ITEM),
                    address,
                    data?.getStringExtra(BaseItemFragment.DESCRIPTION_ITEM),
                    data?.getStringExtra(BaseItemFragment.STATUS_ITEM),
                    data?.getStringExtra(BaseItemFragment.ENTRY_DATE_ITEM),
                    data?.getStringExtra(BaseItemFragment.SALE_DATE_ITEM),
                    data?.getStringExtra(BaseItemFragment.AGENT_ITEM))

            // Create a list of pictures with data from ItemActivity
            val pictureList: ArrayList<Picture?> =
                    data?.getParcelableArrayListExtra<Picture>(BaseItemFragment.PICTURE_LIST_ITEM) as ArrayList<Picture?>

            // Insert item with pictures in database
            itemWithPicturesViewModel.insertItemWithPictures(item, pictureList)
            Log.d(TAG, "item = $item")
            Log.d(TAG, "picture = $pictureList")

        } else {
            Toast.makeText(applicationContext, getString(R.string.real_estate_not_saved), Toast.LENGTH_LONG).show()
        }
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
                // Get MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_nav_drawer_map ->
                // Get MapActivity
                Toast.makeText(this, getString(R.string.map), Toast.LENGTH_SHORT).show()
        }
        this.drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    //----------------------------------------------------------------------------------
    // Private methods to display fragments

    private fun displayListFragment() {
        val listFragment = ListFragment()
        supportFragmentManager.beginTransaction()
                .add(R.id.activity_main_fragment_container_view, listFragment)
                .commit()
    }

    private fun displayDetailsFragment() {
        // Only add DetailFragment in Tablet mode (If found frame_layout_detail)
        if (findViewById<View>(R.id.activity_main_details_fragment_container_view) != null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.activity_main_details_fragment_container_view, detailsFragment)
                    .commit()
        }
    }

    //----------------------------------------------------------------------------------
    // Implement listener from ListFragment to display DetailsFragment when click on an item

    override fun onItemClicked(itemWithPictures: ItemWithPictures?) {

        // Put the item in a bundle and fetch it in detailsFragment
        val bundleItem = Bundle()
        bundleItem.putParcelable(BUNDLE_ITEM_WITH_PICTURES, itemWithPictures)
        detailsFragment.arguments = bundleItem

        // Refresh detailsFragment in Tablet mode
        if (detailsFragment.isVisible) {
            supportFragmentManager.beginTransaction().detach(detailsFragment).commit()
            supportFragmentManager.beginTransaction().attach(detailsFragment).commit()
        } else {
            // Display detailsFragment instead of ListFragment in Phone mode
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_main_fragment_container_view, detailsFragment)
                    .commit()
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
        itemWithPicturesViewModel = ViewModelProvider(this).get(ItemWithPicturesViewModel::class.java)
        displayListFragment()
    }

}