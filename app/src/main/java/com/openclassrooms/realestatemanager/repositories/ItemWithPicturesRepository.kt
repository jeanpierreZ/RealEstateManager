package com.openclassrooms.realestatemanager.repositories

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.database.dao.ItemWithPicturesDao
import com.openclassrooms.realestatemanager.models.Item
import com.openclassrooms.realestatemanager.models.ItemWithPictures
import com.openclassrooms.realestatemanager.models.Picture

// Declares the DAO as a private property in the constructor.
// Pass in the DAO instead of the whole database, because you only need access to the DAO
class ItemWithPicturesRepository(private val itemWithPicturesDao: ItemWithPicturesDao) {

    // --- GET ---
    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val getItemWithPictures: LiveData<List<ItemWithPictures?>> = itemWithPicturesDao.getItemWithPictures()

    // --- CREATE ---

    suspend fun insertItem(item: Item) {
        itemWithPicturesDao.insertItem(item)
    }

    suspend fun insertPictures(pictureList: ArrayList<Picture?>) {
        itemWithPicturesDao.insertPictures(pictureList)
    }

    suspend fun insertItemWithPictures(item: Item, pictureList: ArrayList<Picture?>) {
        itemWithPicturesDao.insertItemWithPictures(item, pictureList)
    }

/*    // --- UPDATE ---
    suspend fun update(itemWithPictures: ItemWithPictures) {
        itemWithPicturesDao.updateItemWithPictures(itemWithPictures)
    }*/

}