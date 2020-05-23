package com.openclassrooms.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.openclassrooms.realestatemanager.database.dao.RealEstateWithMediasDao
import com.openclassrooms.realestatemanager.models.Media
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.utils.converters.ListStringConverter
import com.openclassrooms.realestatemanager.utils.converters.UriConverter

// Annotate class to be a Room Database with tables (entities) of the RealEstate and Media classes
@Database(entities = [RealEstate::class, Media::class], version = 1, exportSchema = false)
// Define the TypeConverters to be used for Uri in Media
@TypeConverters(UriConverter::class, ListStringConverter::class)
abstract class RealEstateWithMediasRoomDatabase : RoomDatabase() {

    // --- DAO ---
    abstract fun realEstateWithMediasDao(): RealEstateWithMediasDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.

        @Volatile
        private var INSTANCE: RealEstateWithMediasRoomDatabase? = null

        fun getDatabase(context: Context): RealEstateWithMediasRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        RealEstateWithMediasRoomDatabase::class.java,
                        "real_estate_with_medias_database")
                        .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
