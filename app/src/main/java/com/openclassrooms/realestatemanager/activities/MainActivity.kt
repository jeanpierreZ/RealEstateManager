package com.openclassrooms.realestatemanager.activities

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.fragments.DetailsFragment
import com.openclassrooms.realestatemanager.fragments.ListFragment
import com.openclassrooms.realestatemanager.models.Item
import com.openclassrooms.realestatemanager.models.ItemWithPictures
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.views.viewmodels.ItemWithPicturesViewModel

class MainActivity : AppCompatActivity(), ListFragment.OnItemClickedListener {

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        // Key for item position
        const val BUNDLE_ITEM_WITH_PICTURES: String = "BUNDLE_ITEM_WITH_PICTURES"
    }

    private val itemActivityRequestCode = 1

    private lateinit var itemWithPicturesViewModel: ItemWithPicturesViewModel

    // For design
    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureToolbar()

        // Use the ViewModelProvider to associate the ViewModel with MainActivity
        itemWithPicturesViewModel = ViewModelProvider(this).get(ItemWithPicturesViewModel::class.java)

        displayFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == itemActivityRequestCode && resultCode == Activity.RESULT_OK) {

            // Create an item with data from ItemActivity
            val item = Item(null, data?.getStringExtra(ItemActivity.TYPE_ITEM),
                    data?.getIntExtra(ItemActivity.PRICE_ITEM, 0))

            // Create a picture with data from ItemActivity
            val picture = Picture(null,
                    "lounge",
                    data?.getStringExtra(ItemActivity.PICTURE_ITEM),
                    null)

            // Insert item with pictures in database
            itemWithPicturesViewModel.insertItemWithPictures(item, picture)
            Log.d(TAG, "item = $item")
            Log.d(TAG, "picture = $picture")

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
    // Private methods to configure design

    private fun displayFragment() {
        val listFragment = ListFragment()
        supportFragmentManager.beginTransaction()
                .add(R.id.activity_main_fragment_container_view, listFragment)
                .commit()
    }

}