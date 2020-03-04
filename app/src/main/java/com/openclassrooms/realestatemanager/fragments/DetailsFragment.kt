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

/**
 * A simple [Fragment] subclass.
 */
class DetailsFragment : Fragment(), ItemPicturesAdapter.PictureListener {

    private var recyclerView: RecyclerView? = null
    private var itemPicturesAdapter: ItemPicturesAdapter? = null

    private var pictureList: ArrayList<String?> = arrayListOf()

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

        // Test to recover the type
        descriptionText.text = item?.type

        surfaceText.text = item?.surface.toString()
        roomText.text = item?.roomsNumber.toString()
        bathroomText.text = item?.bathroomsNumber.toString()
        bedroomText.text = item?.bedroomsNumber.toString()

        val streetNumber = item?.address?.streetNumber.toString()
        val street = item?.address?.street.toString()
        streetText.text = String.format("$streetNumber $street")

        apartmentText.text = item?.address?.apartmentNumber.toString()
        cityText.text = item?.address?.city.toString()
        postalCodeText.text = item?.address?.postalCode.toString()
        countryText.text = item?.address?.country.toString()

        configureRecyclerView()


        // Insert pictures to test the recyclerview
/*
        item?.photo?.add(0, "https://cdn.pixabay.com/photo/2016/03/04/03/54/catherine-deneuve-1235443__340.jpg")
        item?.photo?.add(1, "https://cdn.pixabay.com/photo/2016/03/20/17/19/marylyn-monroe-female-1269011__340.jpg")*/

        pictureList.add("https://cdn.pixabay.com/photo/2016/03/04/03/54/catherine-deneuve-1235443__340.jpg")
        pictureList.add("https://cdn.pixabay.com/photo/2016/03/20/17/19/marylyn-monroe-female-1269011__340.jpg")
        pictureList.add("https://cdn.pixabay.com/photo/2019/08/09/21/20/james-dean-4395893_960_720.jpg")
        pictureList.add("https://cdn.pixabay.com/photo/2019/08/17/13/51/clint-eastwood-4412219__340.jpg"
        )

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

    private fun updateUI(updateList: ArrayList<String?>) {
        itemPicturesAdapter?.setPictures(updateList)
    }

    //----------------------------------------------------------------------------------

    override fun onClickPicture(position: Int) {
        Log.d("DETAILS", "CLICK on picture !")
    }

}
