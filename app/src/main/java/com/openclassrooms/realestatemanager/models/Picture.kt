package com.openclassrooms.realestatemanager.models

import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
/*@Entity(tableName = "picture_table", foreignKeys = [ForeignKey(entity = Item::class,
        parentColumns = ["id"], childColumns = ["itemId"], onUpdate = ForeignKey.CASCADE)])*/
@Entity(tableName = "picture_table")
data class Picture(@PrimaryKey(autoGenerate = true)
                   val pictureId: Long? = null,
                   val pictureLocation: String? = null,
                   val roomPicture: Uri? = null,
                   @ColumnInfo(index = true)
                   var itemId: Long? = null) : Parcelable
