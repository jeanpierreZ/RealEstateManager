package com.openclassrooms.realestatemanager.models

import android.content.ContentValues
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
@Entity(tableName = "real_estate_table")
data class RealEstate(@PrimaryKey(autoGenerate = true)
                      var id: Long? = null,
                      var type: String? = null,
                      var price: Int? = null,
                      var surface: Int? = null,
                      var roomsNumber: Int? = null,
                      var bathroomsNumber: Int? = null,
                      var bedroomsNumber: Int? = null,
                      var pointsOfInterest: ArrayList<String>? = ArrayList(),
                      @Embedded
                      var address: @RawValue Address? = null,
                      var description: String? = null,
                      var status: String? = null,
                      var entryDate: String? = null,
                      var saleDate: String? = null,
                      var agent: String? = null) : Parcelable {

    fun realEstateFromContentValues(values: ContentValues): RealEstate {
        val realEstate = RealEstate()

        if (values.containsKey("id")) realEstate.id = values.getAsLong("id")
        if (values.containsKey("type")) realEstate.type = values.getAsString("type")
        if (values.containsKey("price")) realEstate.price = values.getAsInteger("price")
        if (values.containsKey("surface")) realEstate.surface = values.getAsInteger("surface")
        if (values.containsKey("roomsNumber")) realEstate.roomsNumber = values.getAsInteger("roomsNumber")
        if (values.containsKey("bathroomsNumber")) realEstate.bathroomsNumber = values.getAsInteger("bathroomsNumber")
        if (values.containsKey("bedroomsNumber")) realEstate.bedroomsNumber = values.getAsInteger("bedroomsNumber")
        val getValueFromPointsOfInterest: ArrayList<String> = values.getAsString("pointsOfInterest").split(",") as ArrayList<String>
        if (values.containsKey("pointsOfInterest")) realEstate.pointsOfInterest = getValueFromPointsOfInterest
        if (values.containsKey("description")) realEstate.description = values.getAsString("description")
        if (values.containsKey("status")) realEstate.status = values.getAsString("status")
        if (values.containsKey("entryDate")) realEstate.entryDate = values.getAsString("entryDate")
        if (values.containsKey("saleDate")) realEstate.saleDate = values.getAsString("saleDate")
        if (values.containsKey("agent")) realEstate.agent = values.getAsString("agent")
        if (values.containsKey("streetNumber")) realEstate.address?.streetNumber = values.getAsString("streetNumber")
        if (values.containsKey("street")) realEstate.address?.street = values.getAsString("street")
        if (values.containsKey("apartmentNumber")) realEstate.address?.apartmentNumber = values.getAsString("apartmentNumber")
        if (values.containsKey("district")) realEstate.address?.district = values.getAsString("district")
        if (values.containsKey("city")) realEstate.address?.city = values.getAsString("city")
        if (values.containsKey("postalCode")) realEstate.address?.postalCode = values.getAsString("postalCode")
        if (values.containsKey("country")) realEstate.address?.country = values.getAsString("country")
        if (values.containsKey("latitude")) realEstate.address?.latitude = values.getAsDouble("latitude")
        if (values.containsKey("longitude")) realEstate.address?.longitude = values.getAsDouble("longitude")

        return realEstate
    }

}
