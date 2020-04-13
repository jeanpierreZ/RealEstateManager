package com.openclassrooms.realestatemanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.activities.ItemActivity
import com.openclassrooms.realestatemanager.models.ItemWithPictures
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.utils.MyUtils

/**
 * A simple [Fragment] subclass.
 */
class UpdateItemFragment : BaseItemFragment() {

    private val myUtils = MyUtils()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the parent layout for this fragment
        val view = super.onCreateView(inflater, container, savedInstanceState)

        //----------------------------------------------------------------------------------
        // Set data of the real estate from DetailsFragment

        val title = arguments?.getString(ItemActivity.TITLE)
        titleText.text = title

        // Get data from the itemWithPictures
        val itemWithPictures: ItemWithPictures? = arguments?.getParcelable(ItemActivity.ITEM_WITH_PICTURES)

        //----------------------------------------------------------------------------------
        /* Set the initial data in the properties of the real estate
         The data will only be modified if the user enters something else
         */
        itemId = itemWithPictures?.item?.id

        type = itemWithPictures?.item?.type
        price = itemWithPictures?.item?.price
        surface = itemWithPictures?.item?.surface
        roomsNumber = itemWithPictures?.item?.roomsNumber
        bathroomsNumber = itemWithPictures?.item?.bathroomsNumber
        bedroomsNumber = itemWithPictures?.item?.bedroomsNumber
        pointsOfInterest = itemWithPictures?.item?.pointsOfInterest

        streetNumber = itemWithPictures?.item?.itemAddress?.streetNumber
        street = itemWithPictures?.item?.itemAddress?.street
        apartmentNumber = itemWithPictures?.item?.itemAddress?.apartmentNumber
        district = itemWithPictures?.item?.itemAddress?.district
        city = itemWithPictures?.item?.itemAddress?.city
        postalCode = itemWithPictures?.item?.itemAddress?.postalCode
        country = itemWithPictures?.item?.itemAddress?.country

        status = itemWithPictures?.item?.status
        description = itemWithPictures?.item?.description
        entryDate = itemWithPictures?.item?.entryDate
        saleDate = itemWithPictures?.item?.saleDate
        agent = itemWithPictures?.item?.agent

        //----------------------------------------------------------------------------------
        // Set data in editTexts
        editType?.setText(type)

        myUtils.displayIntegerProperties(price, editPrice)
        myUtils.displayIntegerProperties(surface, editSurface)
        myUtils.displayIntegerProperties(roomsNumber, editRooms)
        myUtils.displayIntegerProperties(bathroomsNumber, editBathrooms)
        myUtils.displayIntegerProperties(bedroomsNumber, editBedrooms)

        val pointsOfInterestList = pointsOfInterest
        val displayPOI = pointsOfInterestList?.joinToString { it -> it }
        editPOI.setText(displayPOI)

        editStreetNumber.setText(streetNumber)
        editStreet.setText(street)
        editApartmentNumber.setText(apartmentNumber)
        editDistrict.setText(district)
        editCity.setText(city)
        editPostalCode.setText(postalCode)
        editCountry.setText(country)

        editDescription.setText(description)
        editStatus.setText(status)
        editEntryDate.setText(entryDate)
        editSaleDate.setText(saleDate)
        editAgent.setText(agent)

        //----------------------------------------------------------------------------------
        // Set the recyclerView
        val pictureList: ArrayList<Picture?> = arrayListOf()
        itemWithPictures?.pictures?.let { pictureList.addAll(it) }
        updatePictureList(pictureList)

        // TODO check why pictures are not saved

        return view
    }

}
