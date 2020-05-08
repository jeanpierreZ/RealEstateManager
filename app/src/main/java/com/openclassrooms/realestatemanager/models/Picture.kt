package com.openclassrooms.realestatemanager.models

import android.content.ContentValues
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
                   var pictureId: Long? = null,
                   var pictureDescription: String? = null,
                   var roomPicture: Uri? = null,
                   @ColumnInfo(index = true)
                   var itemId: Long? = null) : Parcelable {

    // --- UTILS ---

    fun pictureFromContentValues(values: ContentValues): Picture {
        val picture = Picture()

        if (values.containsKey("pictureId")) picture.pictureId = values.getAsLong("pictureId")
        if (values.containsKey("pictureDescription")) picture.pictureDescription = values.getAsString("pictureDescription")
        if (values.containsKey("roomPicture")) picture.roomPicture = values.get("roomPicture") as Uri?
        if (values.containsKey("itemId")) picture.itemId = values.getAsLong("itemId")

        return picture
    }
}
