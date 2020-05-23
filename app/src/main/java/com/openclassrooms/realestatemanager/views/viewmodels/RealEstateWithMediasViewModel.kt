package com.openclassrooms.realestatemanager.views.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.database.RealEstateWithMediasRoomDatabase
import com.openclassrooms.realestatemanager.models.Media
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.models.RealEstateWithMedias
import com.openclassrooms.realestatemanager.repositories.RealEstateWithMediasRepository
import kotlinx.coroutines.launch

class RealEstateWithMediasViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: RealEstateWithMediasRepository

    // --- GET ---

    // LiveData gives us updated realEstateWithMedias when they change.
    val getRealEstateWithMedias: LiveData<List<RealEstateWithMedias?>>

    init {
        // Gets reference to realEstateWithMediasDao from RealEstateWithMediasRoomDatabase to construct
        // the correct RealEstateWithMediasRepository.
        val realEstateWithMediasDao = RealEstateWithMediasRoomDatabase.getDatabase(application).realEstateWithMediasDao()
        repository = RealEstateWithMediasRepository(realEstateWithMediasDao)
        getRealEstateWithMedias = repository.getRealEstateWithMedias
    }

    fun getModifiedRealEstateWithMedias(id: Long?) = repository.getModifiedRealEstateWithMedias(id)

    fun getRealEstateWithMediasFromSearch(query: SupportSQLiteQuery) = repository.getRealEstateWithMediasFromSearch(query)

    // --- CREATE ---

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */

    fun insertRealEstateWithMedias(realEstate: RealEstate, mediaList: ArrayList<Media?>) = viewModelScope.launch {
        repository.insertRealEstateWithMedias(realEstate, mediaList)
    }

    // --- UPDATE ---

    fun updateRealEstateWithMedias(realEstate: RealEstate, mediaList: ArrayList<Media?>) = viewModelScope.launch {
        repository.updateRealEstateWithMedias(realEstate, mediaList)
    }

}
