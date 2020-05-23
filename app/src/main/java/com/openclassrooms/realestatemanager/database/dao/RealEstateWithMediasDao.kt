package com.openclassrooms.realestatemanager.database.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.models.Media
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.models.RealEstateWithMedias
import org.jetbrains.annotations.TestOnly


@Dao
interface RealEstateWithMediasDao {

    //----------------------------------------------------------------------------------
    // --- GET ---

    @Transaction
    @Query("SELECT * FROM real_estate_table ORDER BY id DESC")
    fun getRealEstateWithMedias(): LiveData<List<RealEstateWithMedias?>>

    @Transaction
    @Query("SELECT * FROM real_estate_table WHERE id = :id")
    fun getRealEstateWithMediasFromId(id: Long?): LiveData<RealEstateWithMedias?>

    // Avoid false positive notifications for observable queries
    fun getModifiedRealEstateWithMedias(updatedId: Long?): LiveData<RealEstateWithMedias?> =
            Transformations.distinctUntilChanged(getRealEstateWithMediasFromId(updatedId))

    @Query("SELECT * FROM media_table WHERE realEstateId= :itemId")
    fun getMediaList(itemId: Long?): List<Media?>

    // This method is used in RealEstateWithMediasContentProvider
    @Query("SELECT * FROM real_estate_table JOIN media_table ON realEstateId = id WHERE id = :id")
    fun getRealEstateWithMediasCursor(id: Long): Cursor?

    @RawQuery(observedEntities = [RealEstateWithMedias::class])
    fun getRealEstateWithMediasFromSearch(query: SupportSQLiteQuery): LiveData<List<RealEstateWithMedias>>

    //----------------------------------------------------------------------------------
    // --- CREATE ---

    // --- This method is used in RealEstateWithMediasAndroidTest
    @TestOnly
    @Insert
    fun createRealEstateWithMediasForTest(realEstate: RealEstate, mediaList: ArrayList<Media?>)

    // These methods are used in RealEstateWithMediasContentProvider
    @Insert
    fun createRealEstateWithMediasForContentProvider(realEstate: RealEstate, mediaList: ArrayList<Media?>): Long? {
        val id = createRealEstateForTest(realEstate)
        for (media in mediaList) {
            media?.realEstateId = id
        }
        createMediasForContentProvider(mediaList)
        return realEstate.id
    }

    @Insert
    fun createRealEstateForTest(realEstate: RealEstate): Long

    @Insert
    fun createMediasForContentProvider(mediaList: ArrayList<Media?>)

    // ---

    // "suspend" to the DAO methods to make them asynchronous (Kotlin coroutines functionality)
    @Insert
    suspend fun insertRealEstate(realEstate: RealEstate): Long

    @Insert
    suspend fun insertMedias(mediaList: ArrayList<Media?>)

    @Transaction
    suspend fun insertRealEstateWithMedias(realEstate: RealEstate, mediaList: ArrayList<Media?>) {
        val id = insertRealEstate(realEstate)
        for (media in mediaList) {
            media?.realEstateId = id
        }
        insertMedias(mediaList)
    }

    //----------------------------------------------------------------------------------
    // --- DELETE ---

    @Delete
    suspend fun deleteMedias(mediaList: ArrayList<Media?>)

    //----------------------------------------------------------------------------------
    // --- UPDATE ---

    // This method is used in RealEstateWithMediasAndroidTest
    @TestOnly
    @Update
    fun modifyRealEstateWithMediasForTest(realEstate: RealEstate, mediaList: ArrayList<Media?>)

    @Update
    // "suspend" to the DAO methods to make them asynchronous (Kotlin coroutines functionality)
    suspend fun updateRealEstate(realEstate: RealEstate)

    @Transaction
    suspend fun updateRealEstateWithMedias(realEstate: RealEstate, mediaList: ArrayList<Media?>) {
        updateRealEstate(realEstate)

        // Remove the medias associated with the current RealEstate
        val oldMediaList = getMediaList(realEstate.id) as ArrayList<Media?>
        deleteMedias(oldMediaList)

        // Then insert the new medias
        for (media in mediaList) {
            media?.realEstateId = realEstate.id
        }
        insertMedias(mediaList)
    }

}
