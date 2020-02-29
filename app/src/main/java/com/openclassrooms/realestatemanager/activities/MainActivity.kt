package com.openclassrooms.realestatemanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
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
//                Toast.makeText(this, "add", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ItemActivity::class.java)
                startActivity(intent)
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