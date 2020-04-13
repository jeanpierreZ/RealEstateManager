package com.openclassrooms.realestatemanager.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemAddress(val streetNumber: String? = null,
                       val street: String? = null,
                       val apartmentNumber: String? = null,
                       val district: String? = null,
                       val city: String? = null,
                       val postalCode: String? = null,
                       val country: String? = null) : Parcelable
