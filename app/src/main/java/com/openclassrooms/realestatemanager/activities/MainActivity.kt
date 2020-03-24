package com.openclassrooms.realestatemanager.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.fragments.DetailsFragment
import com.openclassrooms.realestatemanager.fragments.ListFragment
import com.openclassrooms.realestatemanager.models.Address
import com.openclassrooms.realestatemanager.models.Item
import com.openclassrooms.realestatemanager.models.ItemWithPictures
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.views.viewmodels.ItemWithPicturesViewModel
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), ListFragment.OnItemClickedListener, EasyPermissions.PermissionCallbacks {

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        // Key for item position
        const val BUNDLE_ITEM_WITH_PICTURES: String = "BUNDLE_ITEM_WITH_PICTURES"

        // Static data for Permissions
        val PERMS = arrayOf(Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE)

        const val PERMS_REQUEST_CODE = 123
    }

    private val itemActivityRequestCode = 1

    private lateinit var itemWithPicturesViewModel: ItemWithPicturesViewModel

    // For design
    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureToolbar()

        // Request permission when starting MainActivity
        EasyPermissions.requestPermissions(this, getString(R.string.rationale_permission_access),
                PERMS_REQUEST_CODE, *PERMS)

        // Open the view with RestaurantMapFragment if permissions were already allowed
        if (EasyPermissions.hasPermissions(this, *PERMS)) {
            // Use the ViewModelProvider to associate the ViewModel with MainActivity
            itemWithPicturesViewModel = ViewModelProvider(this).get(ItemWithPicturesViewModel::class.java)
            displayFragment()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == itemActivityRequestCode && resultCode == Activity.RESULT_OK) {

            // Create an address with data from ItemActivity
            val address = Address(data?.getStringExtra(ItemActivity.STREET_NUMBER_ITEM),
                    data?.getStringExtra(ItemActivity.STREET_ITEM),
                    data?.getStringExtra(ItemActivity.APARTMENT_NUMBER_ITEM),
                    data?.getStringExtra(ItemActivity.DISTRICT_ITEM),
                    data?.getStringExtra(ItemActivity.CITY_ITEM),
                    data?.getStringExtra(ItemActivity.POSTAL_CODE_ITEM),
                    data?.getStringExtra(ItemActivity.COUNTRY_ITEM))

            // Create an item with data from ItemActivity
            val item = Item(null, data?.getStringExtra(ItemActivity.TYPE_ITEM),
                    data?.getIntExtra(ItemActivity.PRICE_ITEM, 0),
                    data?.getIntExtra(ItemActivity.SURFACE_ITEM, 0),
                    data?.getIntExtra(ItemActivity.ROOMS_ITEM, 0),
                    data?.getIntExtra(ItemActivity.BATHROOMS_ITEM, 0),
                    data?.getIntExtra(ItemActivity.BEDROOMS_ITEM, 0),
                    data?.getStringArrayListExtra(ItemActivity.POI_ITEM),
                    address,
                    data?.getStringExtra(ItemActivity.DESCRIPTION_ITEM),
                    data?.getStringExtra(ItemActivity.STATUS_ITEM),
                    data?.getStringExtra(ItemActivity.ENTRY_DATE_ITEM),
                    data?.getStringExtra(ItemActivity.SALE_DATE_ITEM),
                    data?.getStringExtra(ItemActivity.AGENT_ITEM))

            // Create a picture with data from ItemActivity
            val pictureList: ArrayList<Picture?> =
                    data?.getParcelableArrayListExtra<Picture>(ItemActivity.PICTURE_LIST_ITEM) as ArrayList<Picture?>

            // Insert item with pictures in database
            itemWithPicturesViewModel.insertItemWithPictures(item, pictureList)
            Log.d(TAG, "item = $item")
            Log.d(TAG, "picture = $pictureList")

        } else {
            Toast.makeText(applicationContext, getString(R.string.real_estate_not_saved), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu and add it to the Toolbar
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_toolbar_add -> {
                val intent = Intent(this, ItemActivity::class.java)
                startActivityForResult(intent, itemActivityRequestCode)
                true
            }
            R.id.menu_toolbar_edit -> {
                Toast.makeText(this, "edit", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.menu_toolbar_search -> {
                Toast.makeText(this, "search", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //----------------------------------------------------------------------------------
    // Private methods to configure design

    private fun configureToolbar() {
        // Get the toolbar view inside the activity layout
        toolbar = findViewById(R.id.toolbar)
        // Set the Toolbar
        setSupportActionBar(toolbar)
    }

    private fun displayFragment() {
        val listFragment = ListFragment()
        supportFragmentManager.beginTransaction()
                .add(R.id.activity_main_fragment_container_view, listFragment)
                .commit()
    }

    //----------------------------------------------------------------------------------
    // Implement listener from ListFragment to open DetailsFragment when click on an item

    override fun onItemClicked(itemWithPictures: ItemWithPictures?) {
        val detailsFragment = DetailsFragment()

        // Put the item in a bundle and fetch it in detailsFragment
        val bundleItem = Bundle()
        bundleItem.putParcelable(BUNDLE_ITEM_WITH_PICTURES, itemWithPictures)
        detailsFragment.arguments = bundleItem

        // onItemClicked -> display detailsFragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.activity_main_fragment_container_view, detailsFragment)
                .commit()
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
        displayFragment()
    }

}