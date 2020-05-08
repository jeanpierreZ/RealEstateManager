package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.realestatemanager.database.ItemWithPicturesRoomDatabase
import com.openclassrooms.realestatemanager.database.dao.ItemWithPicturesDao
import com.openclassrooms.realestatemanager.models.Item
import com.openclassrooms.realestatemanager.models.ItemWithPictures
import com.openclassrooms.realestatemanager.models.Picture
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ItemWithPicturesAndroidTest {

    companion object {
        // Set data for test
        private const val ID_TEST: Long = 1
        private const val ID_PICTURE_TEST: Long = 1
        private const val FIRST_AGENT_TEST = "First Agent Test"
        private const val SECOND_AGENT_TEST = "Second Agent Test"
        private val ITEM_TEST = Item(ID_TEST, null, null, null, null, null, null, null, null, null, null, null, null, FIRST_AGENT_TEST)
        private val UPDATE_ITEM_TEST = Item(ID_TEST, null, null, null, null, null, null, null, null, null, null, null, null, SECOND_AGENT_TEST)
        private val PICTURE_TEST = Picture(ID_PICTURE_TEST, null, null, ID_TEST)
        private val ITEMWITHPICTURES_TEST = ItemWithPictures(ITEM_TEST, arrayListOf(PICTURE_TEST))
        private val UPDATE_ITEMWITHPICTURES_TEST = ItemWithPictures(UPDATE_ITEM_TEST, arrayListOf(PICTURE_TEST))
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var itemWithPicturesDaoTest: ItemWithPicturesDao
    private lateinit var db: ItemWithPicturesRoomDatabase
    private val liveDataTestUtil = LiveDataTestUtil()

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, ItemWithPicturesRoomDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()
        itemWithPicturesDaoTest = db.itemWithPicturesDao()

        // Create the data to test
        itemWithPicturesDaoTest.createItemWithPicturesForTest(ITEM_TEST, arrayListOf(PICTURE_TEST))
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun getItemWithPicturesTest() {
        // Retrieve the first item in the list
        val getItemWithPictures = liveDataTestUtil.getValue(itemWithPicturesDaoTest.getItemWithPictures())
        assertEquals(getItemWithPictures?.get(0)?.item, ITEMWITHPICTURES_TEST.item)
    }

    @Test
    @Throws(Exception::class)
    fun getItemWithPicturesFromIdTest() {
        // Test the item
        val getItemWithPicturesId = liveDataTestUtil.getValue(itemWithPicturesDaoTest.getItemWithPicturesFromId(ITEM_TEST.id))
        assertEquals(getItemWithPicturesId?.item?.id, ITEMWITHPICTURES_TEST.item.id)

        // Test the agent
        val getItemWithPicturesAgent = liveDataTestUtil.getValue(itemWithPicturesDaoTest.getItemWithPicturesFromId(ITEM_TEST.id))
        assertEquals(getItemWithPicturesAgent?.item?.agent, ITEMWITHPICTURES_TEST.item.agent)
    }

    @Test
    @Throws(Exception::class)
    fun getModifiedItemWithPicturesTest() {
        // Create the modified data to test
        itemWithPicturesDaoTest.modifyItemWithPicturesForTest(UPDATE_ITEM_TEST, arrayListOf(PICTURE_TEST))

        // Test the modified data (the agent)
        val getItemWithPicturesId = liveDataTestUtil.getValue(itemWithPicturesDaoTest.getModifiedItemWithPictures(UPDATE_ITEM_TEST.id))
        assertEquals(getItemWithPicturesId?.item?.agent, UPDATE_ITEMWITHPICTURES_TEST.item.agent)
    }

}