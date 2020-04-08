package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
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

    @Transaction
    @Query("SELECT * FROM item_table WHERE id = :updatedId")
    fun updateItemWithPictures(updatedId: Long?): LiveData<ItemWithPictures?>

    // Avoid false positive notifications for observable queries
    fun getUpdatedItemWithPictures(updatedId: Long?): LiveData<ItemWithPictures?> =
            Transformations.distinctUntilChanged(updateItemWithPictures(updatedId))

    // --- CREATE ---

    @Insert
    // "suspend" to the DAO methods to make them asynchronous (Kotlin coroutines functionality)
    suspend fun insertItem(item: Item): Long

    @Insert
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
