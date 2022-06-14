package com.switcherette.boarribs.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.switcherette.boarribs.data.Sighting

@Database(entities=[Sighting::class], version=1)
abstract class SightingsDatabase : RoomDatabase() {
    abstract fun sightingDao() : SightingDao
}