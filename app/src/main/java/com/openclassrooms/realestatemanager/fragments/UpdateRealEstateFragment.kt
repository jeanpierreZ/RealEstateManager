package com.openclassrooms.realestatemanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.activities.RealEstateActivity
import com.openclassrooms.realestatemanager.models.RealEstateWithMedias
import com.openclassrooms.realestatemanager.utils.MyUtils

/**
 * A simple [Fragment] subclass.
 */
class UpdateRealEstateFragment : BaseRealEstateFragment() {

    private val myUtils = MyUtils()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the parent layout for this fragment
        val view = super.onCreateView(inflater, container, savedInstanceState)

        //----------------------------------------------------------------------------------
        // Set data of the real estate from DetailsFragment

        val title = arguments?.getString(RealEstateActivity.TITLE_FRAGMENT)
        binding.fragmentBaseRealEstateTitle.text = title

        // Get data from the realEstateWithMedias
        val realEstateWithMedias: RealEstateWithMedias? = arguments?.getParcelable(RealEstateActivity.REAL_ESTATE_WITH_MEDIAS)

        //----------------------------------------------------------------------------------
        /* Set the initial data in the properties of the real estate
         The data will only be modified if the user enters something else
         */
        id = realEstateWithMedias?.realEstate?.id

        type = realEstateWithMedias?.realEstate?.type
        price = realEstateWithMedias?.realEstate?.price
        surface = realEstateWithMedias?.realEstate?.surface
        roomsNumber = realEstateWithMedias?.realEstate?.roomsNumber
        bathroomsNumber = realEstateWithMedias?.realEstate?.bathroomsNumber
        bedroomsNumber = realEstateWithMedias?.realEstate?.bedroomsNumber
        pointsOfInterest = realEstateWithMedias?.realEstate?.pointsOfInterest

        streetNumber = realEstateWithMedias?.realEstate?.address?.streetNumber
        street = realEstateWithMedias?.realEstate?.address?.street
        apartmentNumber = realEstateWithMedias?.realEstate?.address?.apartmentNumber
        district = realEstateWithMedias?.realEstate?.address?.district
        city = realEstateWithMedias?.realEstate?.address?.city
        postalCode = realEstateWithMedias?.realEstate?.address?.postalCode
        country = realEstateWithMedias?.realEstate?.address?.country

        status = realEstateWithMedias?.realEstate?.status
        description = realEstateWithMedias?.realEstate?.description
        entryDate = realEstateWithMedias?.realEstate?.entryDate
        saleDate = realEstateWithMedias?.realEstate?.saleDate
        agent = realEstateWithMedias?.realEstate?.agent

        //----------------------------------------------------------------------------------
        // Set data in editTexts

        with(binding) {
            fragmentBaseRealEstateEditType.setText(type)

            myUtils.displayIntegerProperties(price, fragmentBaseRealEstateEditPrice)
            myUtils.displayIntegerProperties(surface, fragmentBaseRealEstateEditSurface)
            myUtils.displayIntegerProperties(roomsNumber, fragmentBaseRealEstateEditRooms)
            myUtils.displayIntegerProperties(bathroomsNumber, fragmentBaseRealEstateEditBathrooms)
            myUtils.displayIntegerProperties(bedroomsNumber, fragmentBaseRealEstateEditBedrooms)

            val displayPOI = pointsOfInterest?.joinToString { it -> it }
            fragmentBaseRealEstateEditPoi.setText(displayPOI)

            fragmentBaseRealEstateEditStreetNumber.setText(streetNumber)
            fragmentBaseRealEstateEditStreet.setText(street)
            fragmentBaseRealEstateEditApartmentNumber.setText(apartmentNumber)
            fragmentBaseRealEstateEditDistrict.setText(district)
            fragmentBaseRealEstateEditCity.setText(city)
            fragmentBaseRealEstateEditPostalCode.setText(postalCode)
            fragmentBaseRealEstateEditCountry.setText(country)

            fragmentBaseRealEstateEditDescription.setText(description)
            fragmentBaseRealEstateEditStatus.setText(status)
            fragmentBaseRealEstateEditEntryDate.setText(entryDate)
            fragmentBaseRealEstateEditSaleDate.setText(saleDate)
            fragmentBaseRealEstateEditAgent.setText(agent)
        }

        //----------------------------------------------------------------------------------
        // Set the recyclerView
        realEstateWithMedias?.medias?.let { mediaList.addAll(it) }
        updateMediaList(mediaList)

        return view
    }

}
