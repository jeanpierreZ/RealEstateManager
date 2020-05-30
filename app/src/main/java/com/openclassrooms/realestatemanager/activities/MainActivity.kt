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
import com.openclassrooms.realestatemanager.fragments.BaseRealEstateFragment
import com.openclassrooms.realestatemanager.fragments.DetailsFragment
import com.openclassrooms.realestatemanager.fragments.ListFragment
import com.openclassrooms.realestatemanager.fragments.MapFragment
import com.openclassrooms.realestatemanager.models.Address
import com.openclassrooms.realestatemanager.models.Media
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.models.RealEstateWithMedias
import com.openclassrooms.realestatemanager.utils.MyUtils
import com.openclassrooms.realestatemanager.views.viewmodels.RealEstateWithMediasViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*


class MainActivity : AppCompatActivity(),
        ListFragment.OnRealEstateClickedListener,
        MapFragment.OnMarkerClickedListener,
        NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        // Keys for bundle
        const val REAL_ESTATE_ID_USED_FOR_DETAIL: String = "REAL_ESTATE_ID_USED_FOR_DETAIL"
        const val LIST_FROM_SEARCH: String = "LIST_FROM_SEARCH"

        // Key for real estate title
        const val TITLE_REAL_ESTATE_ACTIVITY: String = "TITLE_REAL_ESTATE_ACTIVITY"

        // Request codes
        const val ADD_REAL_ESTATE_ACTIVITY_REQUEST_CODE = 1
        const val UPDATE_REAL_ESTATE_ACTIVITY_REQUEST_CODE = 2
        const val SEARCH_ACTIVITY_REQUEST_CODE = 3
    }

    private lateinit var realEstateWithMediasViewModel: RealEstateWithMediasViewModel

    private val detailsFragment = DetailsFragment()
    private val listFragment = ListFragment()
    private val mapFragment = MapFragment()

    private val myUtils = MyUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureToolbar()
        configureDrawerLayout()
        configureNavigationView()

        // Use the ViewModelProvider to associate the ViewModel with MainActivity
        realEstateWithMediasViewModel = ViewModelProvider(this).get(RealEstateWithMediasViewModel::class.java)

        displayListFragment()
        displayDetailsFragmentAtLaunchInTabletMode()
    }

    //----------------------------------------------------------------------------------

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                ADD_REAL_ESTATE_ACTIVITY_REQUEST_CODE -> {
                    // Insert realEstateWithMedias in database
                    realEstateWithMediasViewModel
                            .insertRealEstateWithMedias(realEstateData(data, null), mediaData(data))
                    sendNotification()
                }

                UPDATE_REAL_ESTATE_ACTIVITY_REQUEST_CODE -> {
                    // Update realEstateWithMedias in database
                    realEstateWithMediasViewModel
                            .updateRealEstateWithMedias(realEstateData(data,
                                    data?.getLongExtra(BaseRealEstateFragment.ID_REAL_ESTATE, 0)),
                                    mediaData(data))
                }

                SEARCH_ACTIVITY_REQUEST_CODE -> {
                    val intentFromSearch =
                            data?.getParcelableArrayListExtra<RealEstateWithMedias>(SearchActivity.SEARCH_LIST)

                    // Add a bundle to listFragment with the list from SearchActivity
                    val bundleListFragment = Bundle()
                    bundleListFragment.putParcelableArrayList(LIST_FROM_SEARCH, intentFromSearch)
                    listFragment.arguments = bundleListFragment

                    // Manage UI for tablet mode
                    if (activityMainDetailsFragmentContainerView != null) {
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
                ADD_REAL_ESTATE_ACTIVITY_REQUEST_CODE -> {
                    myUtils.showShortToastMessage(this, R.string.real_estate_not_saved)
                }
                UPDATE_REAL_ESTATE_ACTIVITY_REQUEST_CODE -> {
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

    private fun realEstateData(data: Intent?, id: Long?): RealEstate {
        // Create an address with data from RealEstateActivity
        val address = Address(data?.getStringExtra(BaseRealEstateFragment.STREET_NUMBER_REAL_ESTATE),
                data?.getStringExtra(BaseRealEstateFragment.STREET_REAL_ESTATE),
                data?.getStringExtra(BaseRealEstateFragment.APARTMENT_NUMBER_REAL_ESTATE),
                data?.getStringExtra(BaseRealEstateFragment.DISTRICT_REAL_ESTATE),
                data?.getStringExtra(BaseRealEstateFragment.CITY_REAL_ESTATE),
                data?.getStringExtra(BaseRealEstateFragment.POSTAL_CODE_REAL_ESTATE),
                data?.getStringExtra(BaseRealEstateFragment.COUNTRY_REAL_ESTATE),
                data?.getDoubleExtra(BaseRealEstateFragment.LATITUDE_REAL_ESTATE, 0.0),
                data?.getDoubleExtra(BaseRealEstateFragment.LONGITUDE_REAL_ESTATE, 0.0))

        // Create an real estate with data from RealEstateActivity
        return RealEstate(id, data?.getStringExtra(BaseRealEstateFragment.TYPE_REAL_ESTATE),
                data?.getIntExtra(BaseRealEstateFragment.PRICE_REAL_ESTATE, -1),
                data?.getIntExtra(BaseRealEstateFragment.SURFACE_REAL_ESTATE, -1),
                data?.getIntExtra(BaseRealEstateFragment.ROOMS_REAL_ESTATE, -1),
                data?.getIntExtra(BaseRealEstateFragment.BATHROOMS_REAL_ESTATE, -1),
                data?.getIntExtra(BaseRealEstateFragment.BEDROOMS_REAL_ESTATE, -1),
                data?.getStringArrayListExtra(BaseRealEstateFragment.POI_REAL_ESTATE),
                address,
                data?.getStringExtra(BaseRealEstateFragment.DESCRIPTION_REAL_ESTATE),
                data?.getStringExtra(BaseRealEstateFragment.STATUS_REAL_ESTATE),
                data?.getStringExtra(BaseRealEstateFragment.ENTRY_DATE_REAL_ESTATE),
                data?.getStringExtra(BaseRealEstateFragment.SALE_DATE_REAL_ESTATE),
                data?.getStringExtra(BaseRealEstateFragment.AGENT_REAL_ESTATE))
    }

    private fun mediaData(data: Intent?): ArrayList<Media?> {
        // Create a list of media with data from RealEstateActivity
        return data?.getParcelableArrayListExtra<Media>(BaseRealEstateFragment.MEDIA_LIST_REAL_ESTATE)
                as ArrayList<Media?>
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
                val intentRealEstate = Intent(this, RealEstateActivity::class.java)
                intentRealEstate.putExtra(TITLE_REAL_ESTATE_ACTIVITY, getString(R.string.create_real_estate))
                startActivityForResult(intentRealEstate, ADD_REAL_ESTATE_ACTIVITY_REQUEST_CODE)
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
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, android.R.color.white)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun configureNavigationView() {
        // For Menu Item
        activityMainNavView.setNavigationItemSelectedListener(this)
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

            R.id.menuNavDrawerMainActivity -> {
                // Check if we are in Tablet mode and adapt UI
                if (activityMainMapFragmentContainerView != null) {
                    if (mapFragment.isVisible) {
                        hideMapFragment()
                    }
                    displayDetailsFragmentAtLaunchInTabletMode()
                }
                displayOrRefreshListFragment()
            }

            R.id.menuNavDrawerMapFragment -> {
                // In Tablet mode, if mapFragment is already called, we adapt UI
                if (mapFragment.isHidden) {
                    hideListAndDetailsFragment()
                    refreshMapFragment()
                } else {
                    displayMapFragment()
                }
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    //----------------------------------------------------------------------------------
    // Private methods to display fragments

    private fun displayListFragment() {
        if (activityMainDetailsFragmentContainerView != null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activityMainFragmentContainerView, listFragment)
                    .attach(listFragment)
                    .show(listFragment)
                    .addToBackStack(listFragment.toString())
                    .commit()
        } else {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activityMainFragmentContainerView, listFragment)
                    .addToBackStack(listFragment.toString())
                    .commit()
        }
    }

    private fun displayDetailsFragmentAtLaunchInTabletMode() {
        // At launch, add only DetailsFragment in Tablet mode,
        // but hide the fragment because no real estate is selected
        if (activityMainDetailsFragmentContainerView != null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activityMainDetailsFragmentContainerView, detailsFragment)
                    .hide(detailsFragment)
                    .commit()
        }
    }

    private fun displayDetailsFragment() {
        // Refresh detailsFragment in Tablet mode
        if (activityMainDetailsFragmentContainerView != null) {
            supportFragmentManager.beginTransaction().detach(detailsFragment).commit()
            // show() is used because detailsFragment is hide at launch
            supportFragmentManager.beginTransaction().attach(detailsFragment).show(detailsFragment)
                    .addToBackStack(detailsFragment.toString())
                    .commit()
        } else {
            // Display detailsFragment instead of ListFragment in Phone mode
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activityMainFragmentContainerView, detailsFragment)
                    .addToBackStack(detailsFragment.toString())
                    .commit()
        }
    }

    private fun displayMapFragment() {
        //  In Tablet mode, display mapFragment at the 1st call
        if (activityMainMapFragmentContainerView != null) {
            // Hide listFragment and detailsFragment
            hideListAndDetailsFragment()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activityMainMapFragmentContainerView, mapFragment)
                    .addToBackStack(mapFragment.toString())
                    .commit()

            // If the toolbar is hide (from PlayerFragment), show it
            if (!supportActionBar?.isShowing!!) {
                supportActionBar?.show()
            }

        } else {
            // Display mapFragment instead of ListFragment or DetailsFragment in Phone mode
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activityMainFragmentContainerView, mapFragment)
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

        // If the toolbar is hide (from PlayerFragment), show it
        if (!supportActionBar?.isShowing!!) {
            supportActionBar?.show()
        }
    }

    private fun refreshListFragment() {
        // show() is used because listFragment is hide
        supportFragmentManager.beginTransaction().detach(listFragment).commit()
        supportFragmentManager.beginTransaction().attach(listFragment).show(listFragment).commit()
    }

    private fun displayOrRefreshListFragment() {
        val currentFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.activityMainFragmentContainerView)
        if (currentFragment is ListFragment) {
            refreshListFragment()
        } else {
            displayListFragment()
        }
    }

    //----------------------------------------------------------------------------------
    // Put the real estate id in a bundle and fetch it in detailsFragment

    private fun putRealEstateIdInBundle(itemWithPicturesId: Long?) {
        val bundleRealEstate = Bundle()
        itemWithPicturesId?.let { bundleRealEstate.putLong(REAL_ESTATE_ID_USED_FOR_DETAIL, it) }
        detailsFragment.arguments = bundleRealEstate
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

    // Implement listener from ListFragment to display DetailsFragment when click on a real estate
    override fun onRealEstateClicked(realEstateWithMediasId: Long?) {
        putRealEstateIdInBundle(realEstateWithMediasId)
        displayDetailsFragment()
    }

    // Implement listener from MapFragment to display DetailsFragment when click on a marker
    override fun onMarkerClicked(realEstateWithMediasId: Long?) {
        // Check if we are in Tablet mode and adapt UI
        if (activityMainMapFragmentContainerView != null) {
            hideMapFragment()
            displayListFragment()
        }
        putRealEstateIdInBundle(realEstateWithMediasId)
        displayDetailsFragment()
    }

}
