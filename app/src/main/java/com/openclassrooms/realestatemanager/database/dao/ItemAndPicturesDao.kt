/*
package com.openclassrooms.realestatemanager.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.openclassrooms.realestatemanager.models.ItemAndPictures


@Dao
interface ItemAndPicturesDao {

    @Transaction
    @Query("SELECT * FROM item_table")
    fun getItemAndPictures(): ArrayList<ItemAndPictures>

    @Insert
    // "suspend" to the DAO methods to make them asynchronous (Kotlin coroutines functionality)
    suspend fun insertItem(itemAndPictures: ItemAndPictures): Long

}*/
