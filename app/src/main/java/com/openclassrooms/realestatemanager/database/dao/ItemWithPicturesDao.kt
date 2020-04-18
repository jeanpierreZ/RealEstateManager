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
    @Query("SELECT * FROM item_table WHERE id = :id")
    fun modifiedItemWithPictures(id: Long?): LiveData<ItemWithPictures?>

    // Avoid false positive notifications for observable queries
    fun getModifiedItemWithPictures(updatedId: Long?): LiveData<ItemWithPictures?> =
            Transformations.distinctUntilChanged(modifiedItemWithPictures(updatedId))

    @Query("SELECT * FROM picture_table WHERE itemId= :itemId")
    fun getPictureList(itemId: Long?): List<Picture?>

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

    // --- DELETE ---

    @Delete
    suspend fun deletePictures(pictureList: ArrayList<Picture?>)

    // --- UPDATE ---

    @Update
    // "suspend" to the DAO methods to make them asynchronous (Kotlin coroutines functionality)
    suspend fun updateItem(item: Item)

    @Transaction
    suspend fun updateItemWithPictures(item: Item, pictureList: ArrayList<Picture?>) {
        updateItem(item)

        // Remove the pictures associated with the current Item
        val oldPictureList = getPictureList(item.id) as ArrayList<Picture?>
        deletePictures(oldPictureList)

        // Then insert the new pictures
        for (picture in pictureList) {
            picture?.itemId = item.id
        }
        insertPictures(pictureList)
    }

}
