package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanager.models.Item


@Dao
interface ItemDao {

    @Query("SELECT * FROM item_table")
    fun getItems(): LiveData<List<Item?>>

    @Insert
    // "suspend" to the DAO methods to make them asynchronous (Kotlin coroutines functionality)
    suspend fun insertItem(item: Item): Long

    @Update
    suspend fun updateItem(item: Item): Int

}