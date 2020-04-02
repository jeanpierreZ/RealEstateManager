package com.openclassrooms.realestatemanager.fragments


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.adapters.ItemPicturesAdapter
import com.openclassrooms.realestatemanager.models.ItemWithPictures
import com.openclassrooms.realestatemanager.models.Picture

/**
 * A simple [Fragment] subclass.
 */
class DetailsFragment : Fragment(), ItemPicturesAdapter.PictureListener, ItemPicturesAdapter.PictureLongClickListener {

    companion object {
        private val TAG = DetailsFragment::class.java.simpleName
    }

    private var recyclerView: RecyclerView? = null
    private var itemPicturesAdapter: ItemPicturesAdapter? = null

    private var pictureList: ArrayList<Picture?> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val fragmentView: View = inflater.inflate(R.layout.fragment_details, container, false)

        val descriptionText: TextView = fragmentView.findViewById(R.id.details_fragment_description)
        val surfaceText: TextView = fragmentView.findViewById(R.id.details_fragment_surface)
        val roomText: TextView = fragmentView.findViewById(R.id.details_fragment_rooms)
        val bathroomText: TextView = fragmentView.findViewById(R.id.details_fragment_bathrooms)
        val bedroomText: TextView = fragmentView.findViewById(R.id.details_fragment_bedrooms)
        val streetText: TextView = fragmentView.findViewById(R.id.details_fragment_street)
        val apartmentText: TextView = fragmentView.findViewById(R.id.details_fragment_apartment)
        val cityText: TextView = fragmentView.findViewById(R.id.details_fragment_city)
        val postalCodeText: TextView = fragmentView.findViewById(R.id.details_fragment_postal_code)
        val countryText: TextView = fragmentView.findViewById(R.id.details_fragment_country)

        // Get RecyclerView from layout and serialise it
        recyclerView = fragmentView.findViewById(R.id.details_fragment_recycler_view)

        // For Toolbar menu
        setHasOptionsMenu(true)

        // Get the itemWithPictures from the bundle
        val itemWithPictures: ItemWithPictures? = arguments?.getParcelable(MainActivity.BUNDLE_ITEM_WITH_PICTURES)

        descriptionText.text = itemWithPictures?.item?.description
        surfaceText.text = itemWithPictures?.item?.surface.toString()
        roomText.text = itemWithPictures?.item?.roomsNumber.toString()
        bathroomText.text = itemWithPictures?.item?.bathroomsNumber.toString()
        bedroomText.text = itemWithPictures?.item?.bedroomsNumber.toString()
        val streetNumber = itemWithPictures?.item?.address?.streetNumber
        val street = itemWithPictures?.item?.address?.street
        streetText.text = String.format("$streetNumber $street")
        apartmentText.text = itemWithPictures?.item?.address?.apartmentNumber
        cityText.text = itemWithPictures?.item?.address?.city
        postalCodeText.text = itemWithPictures?.item?.address?.postalCode
        countryText.text = itemWithPictures?.item?.address?.country

        configureRecyclerView()

        // Clear the pictureList in case of reuse it
        pictureList.clear()

        // Add photos of the chosen real estate from ListFragment
        itemWithPictures?.pictures?.let { pictureList.addAll(it) }

        updateUI(pictureList)
        Log.d(TAG, "pictureList = $pictureList")

        return fragmentView
    }

    //----------------------------------------------------------------------------------
    // Methods for Toolbar Menu

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.menu_toolbar_edit)?.isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_toolbar_edit) {
            Toast.makeText(activity, "edit", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerViews, Adapters & LayoutManager

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of pictures
        itemPicturesAdapter = ItemPicturesAdapter(pictureList, Glide.with(this), this, this)
        // Attach the adapter to the recyclerView to populate pictures
        recyclerView?.adapter = itemPicturesAdapter
        // Set layout manager to position the pictures
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun updateUI(updateList: ArrayList<Picture?>) {
        itemPicturesAdapter?.setPictures(updateList)
    }

    //----------------------------------------------------------------------------------
    // Interfaces for callback from ItemPicturesAdapter

    override fun onClickPicture(position: Int) {
        Log.d("DETAILS", "CLICK on picture !")
    }

    override fun onLongClickItem(position: Int) {
        Log.d("DETAILS", "LONG CLICK on picture !")
    }

}
