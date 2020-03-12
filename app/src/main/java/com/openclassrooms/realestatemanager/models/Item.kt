package com.openclassrooms.realestatemanager.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
@Entity(tableName = "item_table")
data class Item(@PrimaryKey(autoGenerate = true) val id: Long? = null,
                val type: String? = null,
                val price: Int? = null,
                val surface: Int? = null,
                val roomsNumber: Int? = null,
                val bathroomsNumber: Int? = null,
                val bedroomsNumber: Int? = null,
                val description: String? = null,
/*                @Relation
                (parentColumn = "id", entityColumn = "itemId", entity = Item::class)
                var picture: @RawValue ArrayList<Picture>? = null,*/
                @Embedded
                val address: @RawValue Address? = null,
                val district: String? = null,
                @Embedded
                val pointsOfInterest: ArrayList<String>? = null,
                val status: String? = null,
                val entryDate: String? = null,
                val saleDate: String? = null,
                val realEstateAgent: String? = null) : Parcelable