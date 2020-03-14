package com.openclassrooms.realestatemanager.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

@Parcelize
data class ItemWithPictures(
        @Embedded val item: Item,
        @Relation(
                parentColumn = "id",
                entityColumn = "itemId"
        )
        val pictures: List<Picture> = ArrayList()
) : Parcelable
