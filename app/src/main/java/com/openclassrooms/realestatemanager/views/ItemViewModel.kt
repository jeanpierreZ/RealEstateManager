package com.openclassrooms.realestatemanager.views

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.database.ItemRoomDatabase
import com.openclassrooms.realestatemanager.models.Item
import com.openclassrooms.realestatemanager.repositories.ItemRepository
import kotlinx.coroutines.launch

class ItemViewModel(application: Application): AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: ItemRepository
    // LiveData gives us updated items when they change.
    val getItems: LiveData<List<Item?>>

    init {
        // Gets reference to ItemDao from ItemRoomDatabase to construct
        // the correct ItemRepository.
        val itemDao = ItemRoomDatabase.getDatabase(application).itemDao()
        repository = ItemRepository(itemDao)
        getItems = repository.getItems
    }

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */
    fun insert(item: Item) = viewModelScope.launch {
        repository.insert(item)
    }

    fun update(item: Item) = viewModelScope.launch {
        repository.update(item)
    }

}