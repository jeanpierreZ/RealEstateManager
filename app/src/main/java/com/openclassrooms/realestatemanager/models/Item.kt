package com.openclassrooms.realestatemanager.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_table")
data class Item(@PrimaryKey(autoGenerate = true) var id: Int,
                var type: String,
                var price: Int,
                var surface: Int,
                var roomsNumber: Int,
                var description: String,
                var photo: List<String>,
                var address: String,
                var district: String,
                var pointsOfInterest: List<String>,
                var status: String,
                var entryDate: String,
                var saleDate: String,
                var realEstateAgent: String)
{
}