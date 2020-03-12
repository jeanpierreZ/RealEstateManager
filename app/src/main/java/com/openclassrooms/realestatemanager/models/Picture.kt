package com.openclassrooms.realestatemanager.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "picture_table")
data class Picture(@PrimaryKey(autoGenerate = true) val pictureId: Long? = null,
                   val itemId: Long? = null,
                   val pictureLocation: String? = null,
                   val roomPicture: String? = null) : Parcelable
