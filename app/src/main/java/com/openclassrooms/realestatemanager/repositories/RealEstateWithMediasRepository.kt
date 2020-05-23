package com.openclassrooms.realestatemanager.repositories

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.database.dao.RealEstateWithMediasDao
import com.openclassrooms.realestatemanager.models.Media
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.models.RealEstateWithMedias

// Declares the DAO as a private property in the constructor.
// Pass in the DAO instead of the whole database, because you only need access to the DAO
class RealEstateWithMediasRepository(private val realEstateWithMediasDao: RealEstateWithMediasDao) {

    // --- GET ---

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val getRealEstateWithMedias: LiveData<List<RealEstateWithMedias?>> = realEstateWithMediasDao.getRealEstateWithMedias()

    fun getModifiedRealEstateWithMedias(id: Long?): LiveData<RealEstateWithMedias?> {
        return realEstateWithMediasDao.getModifiedRealEstateWithMedias(id)
    }

    fun getRealEstateWithMediasFromSearch(query: SupportSQLiteQuery): LiveData<List<RealEstateWithMedias>> {
        return realEstateWithMediasDao.getRealEstateWithMediasFromSearch(query)
    }

    // --- CREATE ---

    suspend fun insertItem(realEstate: RealEstate) {
        realEstateWithMediasDao.insertRealEstate(realEstate)
    }

    suspend fun insertPictures(mediaList: ArrayList<Media?>) {
        realEstateWithMediasDao.insertMedias(mediaList)
    }

    suspend fun insertRealEstateWithMedias(realEstate: RealEstate, mediaList: ArrayList<Media?>) {
        realEstateWithMediasDao.insertRealEstateWithMedias(realEstate, mediaList)
    }

    // --- UPDATE ---

    suspend fun updateRealEstateWithMedias(realEstate: RealEstate, mediaList: ArrayList<Media?>) {
        realEstateWithMediasDao.updateRealEstateWithMedias(realEstate, mediaList)
    }

}
