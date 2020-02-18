package com.openclassrooms.realestatemanager.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.fragments.ListFragment

class MainActivity : AppCompatActivity() {

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

    // Private methods to configure design

    private fun configureToolbar() {
        // Get the toolbar view inside the activity layout
        toolbar = findViewById(R.id.toolbar)
        // Set the Toolbar
        setSupportActionBar(toolbar)
    }

}