package com.openclassrooms.realestatemanager.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.fragments.DetailsFragment
import com.openclassrooms.realestatemanager.fragments.ListFragment
import com.openclassrooms.realestatemanager.models.Item

class MainActivity : AppCompatActivity(), ListFragment.OnItemClickedListener {

    companion object {
        // Key for item position
        const val BUNDLE_ITEM: String = "bundle_item"
    }

    // For design
    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureToolbar()

        val listFragment = ListFragment()
        supportFragmentManager.beginTransaction()
                .add(R.id.activity_main_fragment_container_view, listFragment)
                .commit()
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

    override fun onItemClicked(item: Item?) {
        val detailsFragment = DetailsFragment()

        // Put the item un a bundle and fetch it in the fragment
        val bundleItem = Bundle()
        bundleItem.putParcelable(BUNDLE_ITEM, item)
        detailsFragment.arguments = bundleItem

        // onItemClicked -> display detailsFragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.activity_main_fragment_container_view, detailsFragment)
                .commit()
    }
}