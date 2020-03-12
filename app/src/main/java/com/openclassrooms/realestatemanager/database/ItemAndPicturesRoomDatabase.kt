/*
package com.openclassrooms.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.openclassrooms.realestatemanager.database.dao.ItemAndPicturesDao
import com.openclassrooms.realestatemanager.database.dao.ItemDao
import com.openclassrooms.realestatemanager.models.Item
import com.openclassrooms.realestatemanager.models.ItemAndPictures

// Annotate class to be a Room Database with a table (entity) of the Item class
@Database(entities = [ItemAndPictures::class], version = 1, exportSchema = false)
abstract class ItemAndPicturesRoomDatabase: RoomDatabase() {

    // --- DAO ---
    abstract fun itemAndPicturesDao(): ItemAndPicturesDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.

        @Volatile
        private var INSTANCE: ItemAndPicturesRoomDatabase? = null

        fun getDatabase(context: Context): ItemAndPicturesRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                ItemAndPicturesRoomDatabase::class.java,
                                "item_and_pictures_database")
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}*/
