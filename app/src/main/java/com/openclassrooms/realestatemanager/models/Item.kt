package com.openclassrooms.realestatemanager.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_table")
data class Item(@PrimaryKey(autoGenerate = true) var id: Long? = null,
                var type: String? = null,
                var price: Int? = null,
                var surface: Int? = null,
                var roomsNumber: Int? = null,
                var description: String? = null,
                @Embedded
                var photo: ArrayList<String>? = null,
                var address: String? = null,
                var district: String? = null,
                @Embedded
                var pointsOfInterest: ArrayList<String>? = null,
                var status: String? = null,
                var entryDate: String? = null,
                var saleDate: String? = null,
                var realEstateAgent: String? = null)
{
}