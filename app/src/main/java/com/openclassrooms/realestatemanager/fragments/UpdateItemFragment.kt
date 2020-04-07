package com.openclassrooms.realestatemanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.activities.ItemActivity
import com.openclassrooms.realestatemanager.models.ItemWithPictures
import com.openclassrooms.realestatemanager.models.Picture

/**
 * A simple [Fragment] subclass.
 */
class UpdateItemFragment : BaseItemFragment() {

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

        // Set data in editTexts
        editType?.setText(itemWithPictures?.item?.type)
        editPrice.setText(itemWithPictures?.item?.price.toString())
        editSurface.setText(itemWithPictures?.item?.surface.toString())
        editRooms.setText(itemWithPictures?.item?.roomsNumber.toString())
        editBathrooms.setText(itemWithPictures?.item?.bathroomsNumber.toString())
        editBedrooms.setText(itemWithPictures?.item?.bedroomsNumber.toString())

        val pointsOfInterestList = itemWithPictures?.item?.pointsOfInterest
        val displayPOI = pointsOfInterestList?.joinToString { it -> it }
        editPOI.setText(displayPOI)

        editStreetNumber.setText(itemWithPictures?.item?.address?.streetNumber)
        editStreet.setText(itemWithPictures?.item?.address?.street)
        editApartmentNumber.setText(itemWithPictures?.item?.address?.apartmentNumber)
        editDistrict.setText(itemWithPictures?.item?.address?.district)
        editCity.setText(itemWithPictures?.item?.address?.city)
        editPostalCode.setText(itemWithPictures?.item?.address?.postalCode)
        editCountry.setText(itemWithPictures?.item?.address?.country)

        editDescription.setText(itemWithPictures?.item?.description)
        editStatus.setText(itemWithPictures?.item?.status)
        editEntryDate.setText(itemWithPictures?.item?.entryDate)
        editSaleDate.setText(itemWithPictures?.item?.saleDate)
        editAgent.setText(itemWithPictures?.item?.realEstateAgent)

        // Set the recyclerView
        val pictureList: ArrayList<Picture?> = arrayListOf()
        itemWithPictures?.pictures?.let { pictureList.addAll(it) }
        updatePictureList(pictureList)

        /* Set the initial data in the properties of the real estate
         The data will only be modified if the user enters something else
         */
        itemId = itemWithPictures?.item?.id

        type = itemWithPictures?.item?.type
        price = itemWithPictures?.item?.price
        surface = itemWithPictures?.item?.surface
        rooms = itemWithPictures?.item?.roomsNumber
        bathrooms = itemWithPictures?.item?.bathroomsNumber
        bedrooms = itemWithPictures?.item?.bedroomsNumber
        pointsOfInterest = itemWithPictures?.item?.pointsOfInterest

        streetNumber = itemWithPictures?.item?.address?.streetNumber
        street = itemWithPictures?.item?.address?.street
        apartmentNumber = itemWithPictures?.item?.address?.apartmentNumber
        district = itemWithPictures?.item?.address?.district
        city = itemWithPictures?.item?.address?.city
        postalCode = itemWithPictures?.item?.address?.postalCode
        country = itemWithPictures?.item?.address?.country

        status = itemWithPictures?.item?.status
        description = itemWithPictures?.item?.description
        entryDate = itemWithPictures?.item?.entryDate
        saleDate = itemWithPictures?.item?.saleDate
        agent = itemWithPictures?.item?.realEstateAgent

        // TODO check why pictures are not saved

        return view
    }

}
