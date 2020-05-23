package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.realestatemanager.database.RealEstateWithMediasRoomDatabase
import com.openclassrooms.realestatemanager.database.dao.RealEstateWithMediasDao
import com.openclassrooms.realestatemanager.models.Media
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.models.RealEstateWithMedias
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RealEstateWithMediasAndroidTest {

    companion object {
        // Set data for test
        private const val ID_TEST: Long = 1
        private const val ID_MEDIA_TEST: Long = 1
        private const val FIRST_AGENT_TEST = "First Agent Test"
        private const val SECOND_AGENT_TEST = "Second Agent Test"
        private val REAL_ESTATE_TEST = RealEstate(ID_TEST, null, null, null, null, null, null, null, null, null, null, null, null, FIRST_AGENT_TEST)
        private val UPDATE_REAL_ESTATE_TEST = RealEstate(ID_TEST, null, null, null, null, null, null, null, null, null, null, null, null, SECOND_AGENT_TEST)
        private val MEDIA_TEST = Media(ID_MEDIA_TEST, null, null, null, ID_TEST)
        private val REAL_ESTATE_WITH_MEDIAS_TEST = RealEstateWithMedias(REAL_ESTATE_TEST, arrayListOf(MEDIA_TEST))
        private val UPDATE_ESTATE_WITH_MEDIAS_TEST = RealEstateWithMedias(UPDATE_REAL_ESTATE_TEST, arrayListOf(MEDIA_TEST))
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var realEstateWithMediasDaoTest: RealEstateWithMediasDao
    private lateinit var db: RealEstateWithMediasRoomDatabase
    private val liveDataTestUtil = LiveDataTestUtil()

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the process is killed.
        db = Room.inMemoryDatabaseBuilder(context, RealEstateWithMediasRoomDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()
        realEstateWithMediasDaoTest = db.realEstateWithMediasDao()

        // Create the data to test
        realEstateWithMediasDaoTest.createRealEstateWithMediasForTest(REAL_ESTATE_TEST, arrayListOf(MEDIA_TEST))
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun getRealEstateWithMediasTest() {
        // Retrieve the first item in the list
        val getRealEstateWithMedias = liveDataTestUtil.getValue(realEstateWithMediasDaoTest.getRealEstateWithMedias())
        assertEquals(getRealEstateWithMedias?.get(0)?.realEstate, REAL_ESTATE_WITH_MEDIAS_TEST.realEstate)
    }

    @Test
    @Throws(Exception::class)
    fun getRealEstateWithMediasFromIdTest() {
        // Test the real estate id
        val getRealEstateWithMediasId = liveDataTestUtil.getValue(realEstateWithMediasDaoTest.getRealEstateWithMediasFromId(REAL_ESTATE_TEST.id))
        assertEquals(getRealEstateWithMediasId?.realEstate?.id, REAL_ESTATE_WITH_MEDIAS_TEST.realEstate.id)

        // Test the agent
        val getRealEstateWithMediasAgent = liveDataTestUtil.getValue(realEstateWithMediasDaoTest.getRealEstateWithMediasFromId(REAL_ESTATE_TEST.id))
        assertEquals(getRealEstateWithMediasAgent?.realEstate?.agent, REAL_ESTATE_WITH_MEDIAS_TEST.realEstate.agent)
    }

    @Test
    @Throws(Exception::class)
    fun getModifiedRealEstateWithMediasTest() {
        // Create the modified data to test
        realEstateWithMediasDaoTest.modifyRealEstateWithMediasForTest(UPDATE_REAL_ESTATE_TEST, arrayListOf(MEDIA_TEST))

        // Test the modified data (the agent)
        val getRealEstateWithMediasId = liveDataTestUtil.getValue(realEstateWithMediasDaoTest.getModifiedRealEstateWithMedias(UPDATE_REAL_ESTATE_TEST.id))
        assertEquals(getRealEstateWithMediasId?.realEstate?.agent, UPDATE_ESTATE_WITH_MEDIAS_TEST.realEstate.agent)
    }

}