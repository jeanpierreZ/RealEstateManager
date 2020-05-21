package com.openclassrooms.realestatemanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.activities.ItemActivity
import com.openclassrooms.realestatemanager.models.ItemWithPictures
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
        binding.fragmentBaseItemTitle.text = title

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
        binding.fragmentBaseItemEditType.setText(type)

        myUtils.displayIntegerProperties(price, binding.fragmentBaseItemEditPrice)
        myUtils.displayIntegerProperties(surface, binding.fragmentBaseItemEditSurface)
        myUtils.displayIntegerProperties(roomsNumber, binding.fragmentBaseItemEditRooms)
        myUtils.displayIntegerProperties(bathroomsNumber, binding.fragmentBaseItemEditBathrooms)
        myUtils.displayIntegerProperties(bedroomsNumber, binding.fragmentBaseItemEditBedrooms)

        val displayPOI = pointsOfInterest?.joinToString { it -> it }
        binding.fragmentBaseItemEditPoi.setText(displayPOI)

        binding.fragmentBaseItemEditStreetNumber.setText(streetNumber)
        binding.fragmentBaseItemEditStreet.setText(street)
        binding.fragmentBaseItemEditApartmentNumber.setText(apartmentNumber)
        binding.fragmentBaseItemEditDistrict.setText(district)
        binding.fragmentBaseItemEditCity.setText(city)
        binding.fragmentBaseItemEditPostalCode.setText(postalCode)
        binding.fragmentBaseItemEditCountry.setText(country)

        binding.fragmentBaseItemEditDescription.setText(description)
        binding.fragmentBaseItemEditStatus.setText(status)
        binding.fragmentBaseItemEditEntryDate.setText(entryDate)
        binding.fragmentBaseItemEditSaleDate.setText(saleDate)
        binding.fragmentBaseItemEditAgent.setText(agent)

        //----------------------------------------------------------------------------------
        // Set the recyclerView
        itemWithPictures?.pictures?.let { pictureList.addAll(it) }
        updatePictureList(pictureList)

        return view
    }

}
