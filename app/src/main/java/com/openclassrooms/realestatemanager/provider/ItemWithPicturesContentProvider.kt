package com.openclassrooms.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.database.ItemWithPicturesRoomDatabase
import com.openclassrooms.realestatemanager.models.Item
import com.openclassrooms.realestatemanager.models.Picture
import org.jetbrains.annotations.TestOnly


class ItemWithPicturesContentProvider : ContentProvider() {

    companion object {
        // For data
        const val AUTHORITY = "${BuildConfig.APPLICATION_ID}.provider"
        private val TABLE_NAME: String = com.openclassrooms.realestatemanager.models.ItemWithPictures::class.java.simpleName
        val URI_ITEM: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }

    @TestOnly
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (context != null) {
            if (values != null) {
                val id: Long? = ItemWithPicturesRoomDatabase.getDatabase(context!!).itemWithPicturesDao()
                        .createItemWithPicturesForContentProvider(Item().itemFromContentValues(values),
                                arrayListOf(Picture().pictureFromContentValues(values)))
                if (id?.toInt() != 0) {
                    context?.contentResolver?.notifyChange(uri, null)
                    return id?.let { ContentUris.withAppendedId(uri, it) }
                }
            }
        }
        throw IllegalArgumentException("Failed to insert row into $uri")
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?,
                       selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        if (context != null) {
            val id = ContentUris.parseId(uri)
            val cursor: Cursor? = ItemWithPicturesRoomDatabase.getDatabase(context!!).itemWithPicturesDao().getItemWithPicturesCursor(id)
            cursor?.setNotificationUri(context?.contentResolver, uri)
            return cursor
        }
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return "vnd.android.cursor.item/$AUTHORITY.$TABLE_NAME"
    }

}
