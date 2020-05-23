package com.openclassrooms.realestatemanager

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.realestatemanager.database.RealEstateWithMediasRoomDatabase
import com.openclassrooms.realestatemanager.provider.RealEstateWithMediasContentProvider.Companion.URI_ITEM
import junit.framework.TestCase.assertNull
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RealEstateWithMediasContentProviderTest {

    companion object {
        // Data set for test
        private const val REAL_ESTATE_ID: Long = 999999
    }

    private var mContentResolver: ContentResolver? = null

    private fun generateRealEstate(): ContentValues {
        val values = ContentValues()
        values.put("id", REAL_ESTATE_ID)
        values.put("type", "Duplex")
        values.put("price", 700000)
        values.put("surface", 80)
        values.put("roomsNumber", 4)
        values.put("bathroomsNumber", 2)
        values.put("bedroomsNumber", 3)
        values.put("pointsOfInterest", "Park, School")
        values.putNull("street")
        values.put("description", "Very nice duplex")
        values.put("status", "Sold")
        values.put("entryDate", "05/04/2020")
        values.put("saleDate", "null")
        values.put("agent", "Test Agent")
        values.put("mediaDescription", "room")
        return values
    }

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        Room.inMemoryDatabaseBuilder(context, RealEstateWithMediasRoomDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        mContentResolver = context.contentResolver
    }

    @Test
    fun numberOfItemsWhenNoItemInserted() {
        val cursor: Cursor? = mContentResolver?.query(ContentUris.withAppendedId(URI_ITEM, REAL_ESTATE_ID), null, null, null, null)
        assertThat(cursor, notNullValue())
        assertThat(cursor?.count, `is`(0))
        cursor?.close()
    }

    @Test
    fun insertAndGetItem() {
        // Before : Add test item
        val userUri: Uri? = mContentResolver?.insert(URI_ITEM, generateRealEstate())

        // Test
        val cursor: Cursor? = mContentResolver?.query(ContentUris.withAppendedId(URI_ITEM, REAL_ESTATE_ID), null, null, null, null)
        assertThat(cursor, notNullValue())
        assertThat(cursor?.count, `is`(1))
        assertThat(cursor?.moveToFirst(), `is`(true))
        assertThat(cursor?.getLong(cursor.getColumnIndexOrThrow("id")), `is`(REAL_ESTATE_ID))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("type")), `is`("Duplex"))
        assertThat(cursor?.getInt(cursor.getColumnIndexOrThrow("price")), `is`(700000))
        assertThat(cursor?.getInt(cursor.getColumnIndexOrThrow("surface")), `is`(80))
        assertThat(cursor?.getInt(cursor.getColumnIndexOrThrow("roomsNumber")), `is`(4))
        assertThat(cursor?.getInt(cursor.getColumnIndexOrThrow("bathroomsNumber")), `is`(2))
        assertThat(cursor?.getInt(cursor.getColumnIndexOrThrow("bedroomsNumber")), `is`(3))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("pointsOfInterest")), `is`("[\"Park\",\" School\"]"))
        assertNull(cursor?.getString(cursor.getColumnIndexOrThrow("street")))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("description")), `is`("Very nice duplex"))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("status")), `is`("Sold"))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("entryDate")), `is`("05/04/2020"))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("saleDate")), `is`("null"))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("agent")), `is`("Test Agent"))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("mediaDescription")), `is`("room"))
        cursor?.close()
    }

}
