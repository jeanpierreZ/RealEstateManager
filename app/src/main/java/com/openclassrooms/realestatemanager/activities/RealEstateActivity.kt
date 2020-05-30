package com.openclassrooms.realestatemanager.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.fragments.AddRealEstateFragment
import com.openclassrooms.realestatemanager.fragments.DetailsFragment
import com.openclassrooms.realestatemanager.fragments.UpdateRealEstateFragment
import com.openclassrooms.realestatemanager.models.RealEstateWithMedias
import com.openclassrooms.realestatemanager.utils.MyUtils
import com.openclassrooms.realestatemanager.utils.dialogfragments.DateDialogFragment
import com.openclassrooms.realestatemanager.utils.dialogfragments.POIDialogFragment
import com.openclassrooms.realestatemanager.utils.dialogfragments.PropertyDialogFragment
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*


class RealEstateActivity : AppCompatActivity(),
        PropertyDialogFragment.OnPropertyChosenListener,
        POIDialogFragment.OnPOIChosenListener,
        DateDialogFragment.OnDateListener {

    companion object {
        // Keys for fragment title and realEstateWithMedias
        const val TITLE_FRAGMENT: String = "TITLE_FRAGMENT"
        const val REAL_ESTATE_WITH_MEDIAS: String = "REAL_ESTATE_WITH_MEDIAS"
    }

    private var title: String? = ""
    private var realEstateWithMedias: RealEstateWithMedias? = null
    private var fragment = Fragment()

    private val myUtils = MyUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_estate)

        title = intent.getStringExtra(MainActivity.TITLE_REAL_ESTATE_ACTIVITY)
        // realEstateWithMedias object from DetailsFragment
        realEstateWithMedias = intent.getParcelableExtra(DetailsFragment.REAL_ESTATE_WITH_MEDIAS)

        configureToolbar()
        displayFragment()
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

    private fun displayFragment() {
        val bundle = Bundle()
        bundle.putString(TITLE_FRAGMENT, title)

        if (title == getString(R.string.create_real_estate)) {
            fragment = AddRealEstateFragment()
        } else {
            fragment = UpdateRealEstateFragment()
            bundle.putParcelable(REAL_ESTATE_WITH_MEDIAS, realEstateWithMedias)
        }

        fragment.arguments = bundle
        supportFragmentManager.beginTransaction()
                .replace(R.id.activity_real_estate_fragment_container_view, fragment)
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
    // Implement listeners from the DialogFragments, use data in BaseRealEstateFragment methods

    override fun onPropertyChosen(propertyChosen: EditText?) {
        if (fragment is AddRealEstateFragment) {
            (fragment as AddRealEstateFragment).setPropertyChosen(propertyChosen)
        } else {
            (fragment as UpdateRealEstateFragment).setPropertyChosen(propertyChosen)
        }
    }

    override fun onPOIChosen(POIChosen: ArrayList<String>?) {
        if (fragment is AddRealEstateFragment) {
            (fragment as AddRealEstateFragment).setPOIChosen(POIChosen)
        } else {
            (fragment as UpdateRealEstateFragment).setPOIChosen(POIChosen)
        }
    }

    override fun onDateChosen(editTextChosen: EditText?) {
        if (fragment is AddRealEstateFragment) {
            (fragment as AddRealEstateFragment).setDateChosen(editTextChosen)
        } else {
            (fragment as UpdateRealEstateFragment).setDateChosen(editTextChosen)
        }
    }

}
