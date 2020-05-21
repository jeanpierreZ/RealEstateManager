package com.openclassrooms.realestatemanager.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.fragments.AddItemFragment
import com.openclassrooms.realestatemanager.fragments.DetailsFragment
import com.openclassrooms.realestatemanager.fragments.UpdateItemFragment
import com.openclassrooms.realestatemanager.models.ItemWithPictures
import com.openclassrooms.realestatemanager.utils.MyUtils
import com.openclassrooms.realestatemanager.utils.dialogfragments.DateDialogFragment
import com.openclassrooms.realestatemanager.utils.dialogfragments.POIDialogFragment
import com.openclassrooms.realestatemanager.utils.dialogfragments.PropertyDialogFragment
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*


class ItemActivity : AppCompatActivity(),
        PropertyDialogFragment.OnPropertyChosenListener,
        POIDialogFragment.OnPOIChosenListener,
        DateDialogFragment.OnDateListener {

    companion object {
        // Keys for fragment title and itemWithPictures
        const val TITLE: String = "TITLE_FRAGMENT"
        const val ITEM_WITH_PICTURES: String = "ITEM_WITH_PICTURES_FRAGMENT"
    }

    private var title: String? = ""
    private var itemWithPictures: ItemWithPictures? = null
    private var fragment = Fragment()

    private val myUtils = MyUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        title = intent.getStringExtra(MainActivity.TITLE_ITEM_ACTIVITY)
        // itemWithPictures object from DetailsFragment
        itemWithPictures = intent.getParcelableExtra(DetailsFragment.BUNDLE_ITEM_WITH_PICTURES)

        configureToolbar()
        displayCreateItemFragment()
    }

    //----------------------------------------------------------------------------------
    // Configure design

    private fun configureToolbar() {
        // Set the Toolbar
        setSupportActionBar(toolbar)
        // Get a support ActionBar corresponding to this toolbar
        val actionBar = supportActionBar
        // Enable the Up button
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun displayCreateItemFragment() {
        val bundle = Bundle()
        bundle.putString(TITLE, title)

        if (title == getString(R.string.create_real_estate)) {
            fragment = AddItemFragment()
        } else {
            fragment = UpdateItemFragment()
            bundle.putParcelable(ITEM_WITH_PICTURES, itemWithPictures)
        }

        fragment.arguments = bundle
        supportFragmentManager.beginTransaction()
                .replace(R.id.activity_item_fragment_container_view, fragment)
                .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Respond to the action bar's Up/Home button
            android.R.id.home ->
                myUtils.showShortToastMessage(this, R.string.real_estate_not_saved)
        }
        return super.onOptionsItemSelected(item)
    }

    //----------------------------------------------------------------------------------
    // Implement listeners from the DialogFragments, use data in BaseItemFragment methods

    override fun onPropertyChosen(propertyChosen: EditText?) {
        if (fragment is AddItemFragment) {
            (fragment as AddItemFragment).setPropertyChosen(propertyChosen)
        } else {
            (fragment as UpdateItemFragment).setPropertyChosen(propertyChosen)
        }
    }

    override fun onPOIChosen(POIChosen: ArrayList<String>?) {
        if (fragment is AddItemFragment) {
            (fragment as AddItemFragment).setPOIChosen(POIChosen)
        } else {
            (fragment as UpdateItemFragment).setPOIChosen(POIChosen)
        }
    }

    override fun onDateChosen(editTextChosen: EditText?) {
        if (fragment is AddItemFragment) {
            (fragment as AddItemFragment).setDateChosen(editTextChosen)
        } else {
            (fragment as UpdateItemFragment).setDateChosen(editTextChosen)
        }
    }

}
