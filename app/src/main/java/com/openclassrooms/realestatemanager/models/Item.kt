package com.openclassrooms.realestatemanager.models

data class Item(var type: String,
                var price: Int,
                var surface: Int,
                var roomsNumber: Int,
                var description: String,
                var photo: List<String>,
                var address: String,
                var pointsOfInterest: List<String>,
                var status: String,
                var entryDate: String,
                var saleDate: String,
                var realEstateAgent: String)
{
}