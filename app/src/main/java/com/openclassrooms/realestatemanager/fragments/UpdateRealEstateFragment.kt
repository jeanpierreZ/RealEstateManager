package com.openclassrooms.realestatemanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.activities.RealEstateActivity
import com.openclassrooms.realestatemanager.models.RealEstateWithMedias
import com.openclassrooms.realestatemanager.utils.MyUtils
import kotlinx.android.synthetic.main.fragment_base_real_estate.*

/**
 * A simple [Fragment] subclass.
 */
class UpdateRealEstateFragment : BaseRealEstateFragment() {

    private val myUtils = MyUtils()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView = super.onCreateView(inflater, container, savedInstanceState)

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
        // Set the recyclerView
        realEstateWithMedias?.medias?.let { mediaList.addAll(it) }
        insertMediaInList(mediaList)

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set data of the real estate from DetailsFragment
        val title = arguments?.getString(RealEstateActivity.TITLE_FRAGMENT)
        fragment_base_real_estate_title.text = title

        //----------------------------------------------------------------------------------
        // Set data in editTexts

        fragment_base_real_estate_edit_type.setText(type)

        myUtils.displayIntegerProperties(price, fragment_base_real_estate_edit_price)
        myUtils.displayIntegerProperties(surface, fragment_base_real_estate_edit_surface)
        myUtils.displayIntegerProperties(roomsNumber, fragment_base_real_estate_edit_rooms)
        myUtils.displayIntegerProperties(bathroomsNumber, fragment_base_real_estate_edit_bathrooms)
        myUtils.displayIntegerProperties(bedroomsNumber, fragment_base_real_estate_edit_bedrooms)

        val displayPOI = pointsOfInterest?.joinToString { it -> it }
        fragment_base_real_estate_edit_poi.setText(displayPOI)

        fragment_base_real_estate_edit_street_number.setText(streetNumber)
        fragment_base_real_estate_edit_street.setText(street)
        fragment_base_real_estate_edit_apartment_number.setText(apartmentNumber)
        fragment_base_real_estate_edit_district.setText(district)
        fragment_base_real_estate_edit_city.setText(city)
        fragment_base_real_estate_edit_postal_code.setText(postalCode)
        fragment_base_real_estate_edit_country.setText(country)

        fragment_base_real_estate_edit_description.setText(description)
        fragment_base_real_estate_edit_status.setText(status)
        fragment_base_real_estate_edit_entry_date.setText(entryDate)
        fragment_base_real_estate_edit_sale_date.setText(saleDate)
        fragment_base_real_estate_edit_agent.setText(agent)
    }

}
