package com.openclassrooms.realestatemanager.repositories

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.database.dao.ItemDao
import com.openclassrooms.realestatemanager.models.Item

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class ItemRepository(private val itemDao: ItemDao) {

    // --- GET ---
    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val getItems: LiveData<List<Item?>> = itemDao.getItems()

    // --- CREATE ---
    suspend fun insert(item: Item) {
        itemDao.insertItem(item)
    }

    // --- UPDATE ---
    suspend fun update(item: Item) {
        itemDao.updateItem(item)
    }
}