package com.openclassrooms.realestatemanager.database.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.models.Item
import com.openclassrooms.realestatemanager.models.ItemWithPictures
import com.openclassrooms.realestatemanager.models.Picture
import org.jetbrains.annotations.TestOnly


@Dao
interface ItemWithPicturesDao {

    //----------------------------------------------------------------------------------
    // --- GET ---

    @Transaction
    @Query("SELECT * FROM item_table")
    fun getItemWithPictures(): LiveData<List<ItemWithPictures?>>

    @Transaction
    @Query("SELECT * FROM item_table WHERE id = :id")
    fun getItemWithPicturesFromId(id: Long?): LiveData<ItemWithPictures?>

    // Avoid false positive notifications for observable queries
    fun getModifiedItemWithPictures(updatedId: Long?): LiveData<ItemWithPictures?> =
            Transformations.distinctUntilChanged(getItemWithPicturesFromId(updatedId))

    @Query("SELECT * FROM picture_table WHERE itemId= :itemId")
    fun getPictureList(itemId: Long?): List<Picture?>

    // This method is used in ItemWithPicturesContentProvider
    @Query("SELECT * FROM item_table JOIN picture_table ON itemId = id WHERE id = :id")
    fun getItemWithPicturesCursor(id: Long): Cursor?

    @RawQuery(observedEntities = [ItemWithPictures::class])
    fun getItemWithPicturesFromSearch(query: SupportSQLiteQuery): LiveData<List<ItemWithPictures>>

    //----------------------------------------------------------------------------------
    // --- CREATE ---

    // --- This method is used in ItemWithPicturesAndroidTest
    @TestOnly
    @Insert
    fun createItemWithPicturesForTest(item: Item, pictureList: ArrayList<Picture?>)

    // These methods are used in ItemWithPicturesContentProvider
    @Insert
    fun createItemWithPicturesForContentProvider(item: Item, pictureList: ArrayList<Picture?>): Long? {
        val id = createItemForTest(item)
        for (picture in pictureList) {
            picture?.itemId = id
        }
        createPicturesForContentProvider(pictureList)
        return item.id
    }

    @Insert
    fun createItemForTest(item: Item): Long

    @Insert
    fun createPicturesForContentProvider(pictureList: ArrayList<Picture?>)

    // ---

    // "suspend" to the DAO methods to make them asynchronous (Kotlin coroutines functionality)
    @Insert
    suspend fun insertItem(item: Item): Long

    @Insert
    suspend fun insertPictures(pictureList: ArrayList<Picture?>)

    @Transaction
    suspend fun insertItemWithPictures(item: Item, pictureList: ArrayList<Picture?>) {
        val id = insertItem(item)
        for (picture in pictureList) {
            picture?.itemId = id
        }
        insertPictures(pictureList)
    }

    //----------------------------------------------------------------------------------
    // --- DELETE ---

    @Delete
    suspend fun deletePictures(pictureList: ArrayList<Picture?>)

    //----------------------------------------------------------------------------------
    // --- UPDATE ---

    // This method is used in ItemWithPicturesAndroidTest
    @TestOnly
    @Update
    fun modifyItemWithPicturesForTest(item: Item, pictureList: ArrayList<Picture?>)

    @Update
    // "suspend" to the DAO methods to make them asynchronous (Kotlin coroutines functionality)
    suspend fun updateItem(item: Item)

    @Transaction
    suspend fun updateItemWithPictures(item: Item, pictureList: ArrayList<Picture?>) {
        updateItem(item)

        // Remove the pictures associated with the current Item
        val oldPictureList = getPictureList(item.id) as ArrayList<Picture?>
        deletePictures(oldPictureList)

        // Then insert the new pictures
        for (picture in pictureList) {
            picture?.itemId = item.id
        }
        insertPictures(pictureList)
    }

}
