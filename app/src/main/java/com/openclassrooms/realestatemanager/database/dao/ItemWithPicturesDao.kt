package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
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
    suspend fun insertPictures(picture: Picture): Long

    @Transaction
    suspend fun insertItemWithPictures(item: Item, picture: Picture) {
        val id = insertItem(item)
        picture.itemId = id
        insertPictures(picture)
    }

    // --- UPDATE ---

/*  @Update
  suspend fun updateItemWithPictures(itemWithPictures: ItemWithPictures): Int*/
}
