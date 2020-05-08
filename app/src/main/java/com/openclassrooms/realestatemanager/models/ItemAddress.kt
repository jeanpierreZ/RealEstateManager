package com.openclassrooms.realestatemanager.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemAddress(var streetNumber: String? = null,
                       var street: String? = null,
                       var apartmentNumber: String? = null,
                       var district: String? = null,
                       var city: String? = null,
                       var postalCode: String? = null,
                       var country: String? = null,
                       var latitude: Double? = null,
                       var longitude: Double? = null) : Parcelable
