package com.openclassrooms.realestatemanager.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.fragments.AddItemFragment


class ItemActivity : AppCompatActivity() {

    companion object {
        // Key for fragment title
        const val TITLE_FRAGMENT: String = "TITLE_FRAGMENT"
    }

    private var title: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        title = intent.getStringExtra(MainActivity.TITLE_ITEM_ACTIVITY)

        configureToolbar()
        displayCreateItemFragment()
    }

    //----------------------------------------------------------------------------------

    private fun configureToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        // Set the Toolbar
        setSupportActionBar(toolbar)
        // Get a support ActionBar corresponding to this toolbar
        val actionBar = supportActionBar
        // Enable the Up button
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun displayCreateItemFragment() {
        val bundleTitle = Bundle()
        bundleTitle.putString(TITLE_FRAGMENT, title)

        val addItemFragment = AddItemFragment()
        addItemFragment.arguments = bundleTitle

        supportFragmentManager.beginTransaction()
                .add(R.id.activity_item_fragment_container_view, addItemFragment)
                .commit()
    }

}
