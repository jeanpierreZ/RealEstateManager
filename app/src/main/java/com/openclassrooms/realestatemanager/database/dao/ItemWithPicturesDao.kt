package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.models.Item
import com.openclassrooms.realestatemanager.models.ItemWithPictures
import com.openclassrooms.realestatemanager.models.Picture


@Dao
interface ItemWithPicturesDao {

    // --- GET ---

    @Transaction
    @Query("SELECT * FROM item_table")
    fun getItemWithPictures(): LiveData<List<ItemWithPictures?>>

    // --- CREATE ---

    @Insert
    // "suspend" to the DAO methods to make them asynchronous (Kotlin coroutines functionality)
    suspend fun insertItem(item: Item): Long

    @Insert
    // "suspend" to the DAO methods to make them asynchronous (Kotlin coroutines functionality)
    suspend fun insertPictures(pictureList: ArrayList<Picture?>)

    @Transaction
    suspend fun insertItemWithPictures(item: Item, pictureList: ArrayList<Picture?>) {
        val id = insertItem(item)
        for (picture in pictureList) {
            picture?.itemId = id
        }
        insertPictures(pictureList)
    }

    // --- UPDATE ---

    @Update
    suspend fun updateItemWithPictures(item: Item, pictureList: ArrayList<Picture?>): Int

}
