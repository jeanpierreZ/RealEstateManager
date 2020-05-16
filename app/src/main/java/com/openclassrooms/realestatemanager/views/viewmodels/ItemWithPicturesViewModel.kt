package com.openclassrooms.realestatemanager.views.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.database.ItemWithPicturesRoomDatabase
import com.openclassrooms.realestatemanager.models.Item
import com.openclassrooms.realestatemanager.models.ItemWithPictures
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.repositories.ItemWithPicturesRepository
import kotlinx.coroutines.launch

class ItemWithPicturesViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: ItemWithPicturesRepository

    // --- GET ---

    // LiveData gives us updated itemWithPictures when they change.
    val getItemWithPictures: LiveData<List<ItemWithPictures?>>

    init {
        // Gets reference to itemWithPicturesDao from ItemWithPicturesRoomDatabase to construct
        // the correct ItemWithPicturesRepository.
        val itemWithPicturesDao = ItemWithPicturesRoomDatabase.getDatabase(application).itemWithPicturesDao()
        repository = ItemWithPicturesRepository(itemWithPicturesDao)
        getItemWithPictures = repository.getItemWithPictures
    }

    fun getModifiedItemWithPictures(id: Long?) = repository.getModifiedItemWithPictures(id)

    fun getItemWithPicturesFromSearch(query: SupportSQLiteQuery) = repository.getItemWithPicturesFromSearch(query)

    // --- CREATE ---

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */

    fun insertItemWithPictures(item: Item, pictureList: ArrayList<Picture?>) = viewModelScope.launch {
        repository.insertItemWithPictures(item, pictureList)
    }

    // --- UPDATE ---

    fun updateItemWithPictures(item: Item, pictureList: ArrayList<Picture?>) = viewModelScope.launch {
        repository.updateItemWithPictures(item, pictureList)
    }

}
