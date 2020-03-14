package com.openclassrooms.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.openclassrooms.realestatemanager.database.dao.ItemWithPicturesDao
import com.openclassrooms.realestatemanager.models.Item
import com.openclassrooms.realestatemanager.models.Picture

// Annotate class to be a Room Database with tables (entities) of the Item and Picture classes
@Database(entities = [Item::class, Picture::class], version = 1, exportSchema = false)
abstract class ItemWithPicturesRoomDatabase: RoomDatabase() {

    // --- DAO ---
    abstract fun itemWithPicturesDao(): ItemWithPicturesDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.

        @Volatile
        private var INSTANCE: ItemWithPicturesRoomDatabase? = null

        fun getDatabase(context: Context): ItemWithPicturesRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                ItemWithPicturesRoomDatabase::class.java,
                                "item_with_pictures_database")
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
