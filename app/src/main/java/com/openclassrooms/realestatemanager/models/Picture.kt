package com.openclassrooms.realestatemanager.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Picture(var pictureLocation: String? = null,
                   var roomPicture: String? = null) : Parcelable
