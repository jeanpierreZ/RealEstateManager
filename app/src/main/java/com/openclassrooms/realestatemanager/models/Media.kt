package com.openclassrooms.realestatemanager.models

import android.content.ContentValues
import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
/*@Entity(tableName = "media_table", foreignKeys = [ForeignKey(entity = RealEstate::class,
        parentColumns = ["id"], childColumns = ["realEstateId"], onUpdate = ForeignKey.CASCADE)])*/
@Entity(tableName = "media_table")
data class Media(@PrimaryKey(autoGenerate = true)
                 var mediaId: Long? = null,
                 var mediaDescription: String? = null,
                 var mediaPicture: Uri? = null,
                 var mediaVideo: Uri? = null,
                 @ColumnInfo(index = true)
                 var realEstateId: Long? = null) : Parcelable {

    // --- UTILS ---

    fun mediaFromContentValues(values: ContentValues): Media {
        val media = Media()
        if (values.containsKey("mediaId")) media.mediaId = values.getAsLong("mediaId")
        if (values.containsKey("mediaDescription")) media.mediaDescription = values.getAsString("mediaDescription")
        if (values.containsKey("mediaPicture")) media.mediaPicture = values.get("mediaPicture") as Uri?
        if (values.containsKey("mediaVideo")) media.mediaVideo = values.get("mediaVideo") as Uri?
        if (values.containsKey("realEstateId")) media.realEstateId = values.getAsLong("realEstateId")

        return media
    }
}
