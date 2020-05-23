package com.openclassrooms.realestatemanager.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

@Parcelize
data class RealEstateWithMedias(
        @Embedded val realEstate: RealEstate,
        @Relation(
                parentColumn = "id",
                entityColumn = "realEstateId"
        )
        val medias: List<Media> = ArrayList()
) : Parcelable
