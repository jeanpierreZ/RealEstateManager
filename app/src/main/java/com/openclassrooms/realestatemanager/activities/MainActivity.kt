package com.openclassrooms.realestatemanager.activities

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.application.MyApplication
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.fragments.BaseItemFragment
import com.openclassrooms.realestatemanager.fragments.DetailsFragment
import com.openclassrooms.realestatemanager.fragments.ListFragment
import com.openclassrooms.realestatemanager.fragments.MapFragment
import com.openclassrooms.realestatemanager.models.Item
import com.openclassrooms.realestatemanager.models.ItemAddress
import com.openclassrooms.realestatemanager.models.ItemWithPictures
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.utils.MyUtils
import com.openclassrooms.realestatemanager.views.viewmodels.ItemWithPicturesViewModel
import kotlinx.android.synthetic.main.toolbar.*


class MainActivity : AppCompatActivity(),
        ListFragment.OnItemClickedListener,
        MapFragment.OnMarkerClickedListener,
        NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        // Keys for bundle
        const val ITEM_ID_FOR_DETAIL: String = "ITEM_ID_FOR_DETAIL"
        const val LIST_FROM_SEARCH: String = "LIST_FROM_SEARCH"

        // Key for item title
        const val TITLE_ITEM_ACTIVITY: String = "TITLE_ITEM_ACTIVITY"

        // Request codes
        const val ADD_ITEM_ACTIVITY_REQUEST_CODE = 1
        const val UPDATE_ITEM_ACTIVITY_REQUEST_CODE = 2
        const val SEARCH_ACTIVITY_REQUEST_CODE = 3
    }

    private lateinit var itemWithPicturesViewModel: ItemWithPicturesViewModel

    private val detailsFragment = DetailsFragment()
    private val listFragment = ListFragment()
    private val mapFragment = MapFragment()

    private val myUtils = MyUtils()

    // View binding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        configureToolbar()
        configureDrawerLayout()
        configureNavigationView()

        // Use the ViewModelProvider to associate the ViewModel with MainActivity
        itemWithPicturesViewModel = ViewModelProvider(this).get(ItemWithPicturesViewModel::class.java)

        displayListFragment()
        displayDetailsFragmentAtLaunchInTabletMode()
    }

    //----------------------------------------------------------------------------------

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                ADD_ITEM_ACTIVITY_REQUEST_CODE -> {
                    // Insert itemWithPictures in database
                    itemWithPicturesViewModel
                            .insertItemWithPictures(itemData(data, null), pictureData(data))
                    sendNotification()
                }

                UPDATE_ITEM_ACTIVITY_REQUEST_CODE -> {
                    // Update itemWithPictures in database
                    itemWithPicturesViewModel
                            .updateItemWithPictures(itemData(data,
                                    data?.getLongExtra(BaseItemFragment.ID_ITEM, 0)),
                                    pictureData(data))
                }

                SEARCH_ACTIVITY_REQUEST_CODE -> {
                    val intentFromSearch = data?.getParcelableArrayListExtra<ItemWithPictures>(SearchActivity.SEARCH_LIST)

                    // Add a bundle to listFragment with the list from SearchActivity
                    val bundleListFragment = Bundle()
                    bundleListFragment.putParcelableArrayList(LIST_FROM_SEARCH, intentFromSearch)
                    listFragment.arguments = bundleListFragment

                    // Manage UI for tablet mode
                    if (binding.activityMainDetailsFragmentContainerView != null) {
                        if (mapFragment.isVisible) {
                            hideMapFragment()
                        }
                        if (detailsFragment.isVisible) {
                            hideDetailsFragment()
                        }
                    }
                    displayOrRefreshListFragment()
                }
            }
        } else {
            // If resultCode is canceled show the appropriate message
            when (requestCode) {
                ADD_ITEM_ACTIVITY_REQUEST_CODE -> {
                    myUtils.showShortToastMessage(this, R.string.real_estate_not_saved)
                }
                UPDATE_ITEM_ACTIVITY_REQUEST_CODE -> {
                    myUtils.showShortToastMessage(this, R.string.real_estate_not_saved)
                }
                SEARCH_ACTIVITY_REQUEST_CODE -> {
                    myUtils.showShortToastMessage(this, R.string.search_canceled)
                }
            }
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
                val intentItem = Intent(this, ItemActivity::class.java)
                intentItem.putExtra(TITLE_ITEM_ACTIVITY, getString(R.string.create_real_estate))
                startActivityForResult(intentItem, ADD_ITEM_ACTIVITY_REQUEST_CODE)
            }
            R.id.menu_toolbar_search -> {
                val intentSearch = Intent(this, SearchActivity::class.java)
                startActivityForResult(intentSearch, SEARCH_ACTIVITY_REQUEST_CODE)
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
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, android.R.color.white)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun configureNavigationView() {
        // For Menu Item
        binding.activityMainNavView.setNavigationItemSelectedListener(this)
    }

    //----------------------------------------------------------------------------------
    // Methods for NavigationView in NavigationDrawer

    override fun onBackPressed() {
        // Handle back click to close menu
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle Navigation Item Click
        when (item.itemId) {

            R.id.menu_nav_drawer_main_activity -> {
                // Check if we are in Tablet mode and adapt UI
                if (binding.activityMainMapFragmentContainerView != null) {
                    if (mapFragment.isVisible) {
                        hideMapFragment()
                    }
                    displayDetailsFragmentAtLaunchInTabletMode()
                }
                displayOrRefreshListFragment()
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
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    //----------------------------------------------------------------------------------
    // Private methods to display fragments

    private fun displayListFragment() {
        if (binding.activityMainDetailsFragmentContainerView != null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_main_fragment_container_view, listFragment)
                    .attach(listFragment)
                    .show(listFragment)
                    .addToBackStack(listFragment.toString())
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
        if (binding.activityMainDetailsFragmentContainerView != null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_main_details_fragment_container_view, detailsFragment)
                    .hide(detailsFragment)
                    .commit()
        }
    }

    private fun displayDetailsFragment() {
        // Refresh detailsFragment in Tablet mode
        if (binding.activityMainDetailsFragmentContainerView != null) {
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
        if (binding.activityMainMapFragmentContainerView != null) {
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

    private fun hideDetailsFragment() {
        supportFragmentManager.beginTransaction().hide(detailsFragment).commit()
    }

    private fun hideMapFragment() {
        supportFragmentManager.beginTransaction().hide(mapFragment).commit()
    }

    private fun refreshMapFragment() {
        supportFragmentManager.beginTransaction().detach(mapFragment).commit()
        // show() is used because mapFragment is hide
        supportFragmentManager.beginTransaction().attach(mapFragment).show(mapFragment)
                .addToBackStack(mapFragment.toString())
                .commit()
    }

    private fun refreshListFragment() {
        // show() is used because listFragment is hide
        supportFragmentManager.beginTransaction().detach(listFragment).commit()
        supportFragmentManager.beginTransaction().attach(listFragment).show(listFragment).commit()
    }

    private fun displayOrRefreshListFragment() {
        val currentFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.activity_main_fragment_container_view)
        if (currentFragment is ListFragment) {
            refreshListFragment()
        } else {
            displayListFragment()
        }
    }

    //----------------------------------------------------------------------------------
    // Put the item id in a bundle and fetch it in detailsFragment

    private fun putIdInBundle(itemWithPicturesId: Long?) {
        val bundleItem = Bundle()
        itemWithPicturesId?.let { bundleItem.putLong(ITEM_ID_FOR_DETAIL, it) }
        detailsFragment.arguments = bundleItem
    }

    //----------------------------------------------------------------------------------
    // Display a notification when a new real estate is added
    private fun sendNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val text = getString(R.string.notification_text)
        val dismissPendingIntent = PendingIntent.getActivity(this, 0, Intent(), 0)
        // Set the content and channel of the notification
        val notification = NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_event_note_black_24dp)
                .setContentText(text)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(dismissPendingIntent)
                .setAutoCancel(true)
                .build()
        // Notify the builder
        notificationManager.notify(0, notification)
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
        if (binding.activityMainMapFragmentContainerView != null) {
            hideMapFragment()
            displayListFragment()
        }
        putIdInBundle(itemWithPicturesId)
        displayDetailsFragment()
    }

}
