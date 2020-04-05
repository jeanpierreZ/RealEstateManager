package com.openclassrooms.realestatemanager.utils.converters

import android.net.Uri
import androidx.room.TypeConverter

class UriConverter {

    @TypeConverter
    fun stringToUri(uri: String?): Uri = Uri.parse(uri)

    @TypeConverter
    fun uriToString(uri: Uri?) = uri.toString()

}