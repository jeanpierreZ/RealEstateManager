package com.openclassrooms.realestatemanager.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.ItemActivity
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.adapters.ItemPicturesAdapter
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.views.viewmodels.ItemWithPicturesViewModel

/**
 * A simple [Fragment] subclass.
 */
class DetailsFragment : Fragment(),
        ItemPicturesAdapter.PictureListener,
        ItemPicturesAdapter.PictureLongClickListener {

    companion object {
        private val TAG = DetailsFragment::class.java.simpleName

        // Key for itemWithPictures
        const val BUNDLE_ITEM_WITH_PICTURES: String = "BUNDLE_ITEM_WITH_PICTURES"
    }

    private var recyclerView: RecyclerView? = null
    private var itemPicturesAdapter: ItemPicturesAdapter? = null
    private var pictureList: ArrayList<Picture?> = arrayListOf()

    private var editIntent = Intent()

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
        recyclerView = fragmentView.findViewById(R.id.details_fragment_recycler_view)

        // For Toolbar menu
        setHasOptionsMenu(true)

        editIntent = Intent(activity, ItemActivity::class.java)

        // Get the identifier of the itemWithPictures from the bundle
        val itemWithPicturesId: Long? = arguments?.getLong(MainActivity.BUNDLE_ITEM_ID, -1)

        // Use the ViewModelProvider to associate the ViewModel with DetailsFragment
        val itemWithPicturesViewModel = ViewModelProvider(this).get(ItemWithPicturesViewModel::class.java)

        // Observe changes in the itemWithPictures retrieved with the item id stored in the bundle
        itemWithPicturesViewModel.getUpdatedItemWithPictures(itemWithPicturesId)
                .observe(viewLifecycleOwner, Observer { itemWithPictures ->

                    // Put the itemWithPictures in the intent, which is used when the user want to edit data
                    editIntent.putExtra(BUNDLE_ITEM_WITH_PICTURES, itemWithPictures)

                    // Set the editTexts
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
                })

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
            editIntent.putExtra(MainActivity.TITLE_ITEM_ACTIVITY, getString(R.string.update_real_estate))
            activity?.startActivityForResult(editIntent, MainActivity.UPDATE_ITEM_ACTIVITY_REQUEST_CODE)
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
