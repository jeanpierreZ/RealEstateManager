package com.openclassrooms.realestatemanager.models

import android.content.ContentValues
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
@Entity(tableName = "item_table")
data class Item(@PrimaryKey(autoGenerate = true)
                var id: Long? = null,
                var type: String? = null,
                var price: Int? = null,
                var surface: Int? = null,
                var roomsNumber: Int? = null,
                var bathroomsNumber: Int? = null,
                var bedroomsNumber: Int? = null,
                var pointsOfInterest: ArrayList<String>? = ArrayList(),
                @Embedded
                var itemAddress: @RawValue ItemAddress? = null,
                var description: String? = null,
                var status: String? = null,
                var entryDate: String? = null,
                var saleDate: String? = null,
                var agent: String? = null) : Parcelable {

    fun itemFromContentValues(values: ContentValues): Item {
        val item = Item()

        if (values.containsKey("id")) item.id = values.getAsLong("id")
        if (values.containsKey("type")) item.type = values.getAsString("type")
        if (values.containsKey("price")) item.price = values.getAsInteger("price")
        if (values.containsKey("surface")) item.surface = values.getAsInteger("surface")
        if (values.containsKey("roomsNumber")) item.roomsNumber = values.getAsInteger("roomsNumber")
        if (values.containsKey("bathroomsNumber")) item.bathroomsNumber = values.getAsInteger("bathroomsNumber")
        if (values.containsKey("bedroomsNumber")) item.bedroomsNumber = values.getAsInteger("bedroomsNumber")
        val getValueFromPointsOfInterest: ArrayList<String> = values.getAsString("pointsOfInterest").split(",") as ArrayList<String>
        if (values.containsKey("pointsOfInterest")) item.pointsOfInterest = getValueFromPointsOfInterest
        if (values.containsKey("description")) item.description = values.getAsString("description")
        if (values.containsKey("status")) item.status = values.getAsString("status")
        if (values.containsKey("entryDate")) item.entryDate = values.getAsString("entryDate")
        if (values.containsKey("saleDate")) item.saleDate = values.getAsString("saleDate")
        if (values.containsKey("agent")) item.agent = values.getAsString("agent")
        if (values.containsKey("streetNumber")) item.itemAddress?.streetNumber = values.getAsString("streetNumber")
        if (values.containsKey("street")) item.itemAddress?.street = values.getAsString("street")
        if (values.containsKey("apartmentNumber")) item.itemAddress?.apartmentNumber = values.getAsString("apartmentNumber")
        if (values.containsKey("district")) item.itemAddress?.district = values.getAsString("district")
        if (values.containsKey("city")) item.itemAddress?.city = values.getAsString("city")
        if (values.containsKey("postalCode")) item.itemAddress?.postalCode = values.getAsString("postalCode")
        if (values.containsKey("country")) item.itemAddress?.country = values.getAsString("country")
        if (values.containsKey("latitude")) item.itemAddress?.latitude = values.getAsDouble("latitude")
        if (values.containsKey("longitude")) item.itemAddress?.longitude = values.getAsDouble("longitude")

        return item
    }

}
