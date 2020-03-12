package com.openclassrooms.realestatemanager.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.MainActivity.Companion.BUNDLE_ITEM
import com.openclassrooms.realestatemanager.adapters.ItemPicturesAdapter
import com.openclassrooms.realestatemanager.models.Item
import com.openclassrooms.realestatemanager.models.Picture

/**
 * A simple [Fragment] subclass.
 */
class DetailsFragment : Fragment(), ItemPicturesAdapter.PictureListener {

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

        // Recover the item from the bundle
        val item: Item? = arguments?.getParcelable(BUNDLE_ITEM)

        // Test to retrieve the type and the price
        val t = item?.type
        val p = item?.price
        descriptionText.text = String.format("$t $p")

        surfaceText.text = item?.surface.toString()
        roomText.text = item?.roomsNumber.toString()
        bathroomText.text = item?.bathroomsNumber.toString()
        bedroomText.text = item?.bedroomsNumber.toString()

        val streetNumber = item?.address?.streetNumber
        val street = item?.address?.street
        streetText.text = String.format("$streetNumber $street")

        apartmentText.text = item?.address?.apartmentNumber
        cityText.text = item?.address?.city
        postalCodeText.text = item?.address?.postalCode
        countryText.text = item?.address?.country

        configureRecyclerView()

//        Log.d("DETAILS", "item.picture = ${item?.picture}")
//        pictureList.add(0, item?.picture?.get(0))

        updateUI(pictureList)
        Log.d("DETAILS", "pictureList = $pictureList")

        return fragmentView
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerViews, Adapters & LayoutManager

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of pictures
        itemPicturesAdapter = ItemPicturesAdapter(pictureList, Glide.with(this), this)
        // Attach the adapter to the recyclerView to populate pictures
        recyclerView?.adapter = itemPicturesAdapter
        // Set layout manager to position the pictures
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun updateUI(updateList: ArrayList<Picture?>) {
        itemPicturesAdapter?.setPictures(updateList)
    }

    //----------------------------------------------------------------------------------

    override fun onClickPicture(position: Int) {
        Log.d("DETAILS", "CLICK on picture !")
    }

}
