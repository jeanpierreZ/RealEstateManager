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

        fragment_base_real_estate_type.editText?.setText(type)

        myUtils.displayIntegerProperties(price, fragment_base_real_estate_price.editText)
        myUtils.displayIntegerProperties(surface, fragment_base_real_estate_surface.editText)
        myUtils.displayIntegerProperties(roomsNumber, fragment_base_real_estate_rooms.editText)
        myUtils.displayIntegerProperties(bathroomsNumber, fragment_base_real_estate_bathrooms.editText)
        myUtils.displayIntegerProperties(bedroomsNumber, fragment_base_real_estate_bedrooms.editText)

        val displayPOI = pointsOfInterest?.joinToString { it -> it }
        fragment_base_real_estate_poi.editText?.setText(displayPOI)

        fragment_base_real_estate_street_number.editText?.setText(streetNumber)
        fragment_base_real_estate_street.editText?.setText(street)
        fragment_base_real_estate_apartment_number.editText?.setText(apartmentNumber)
        fragment_base_real_estate_district.editText?.setText(district)
        fragment_base_real_estate_city.editText?.setText(city)
        fragment_base_real_estate_postal_code.editText?.setText(postalCode)
        fragment_base_real_estate_country.editText?.setText(country)

        fragment_base_real_estate_description.editText?.setText(description)
        fragment_base_real_estate_status.editText?.setText(status)
        fragment_base_real_estate_entry_date.editText?.setText(entryDate)
        fragment_base_real_estate_sale_date.editText?.setText(saleDate)
        fragment_base_real_estate_agent.editText?.setText(agent)
    }

}
