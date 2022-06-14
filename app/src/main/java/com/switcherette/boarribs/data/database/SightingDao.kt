package com.switcherette.boarribs.data.database

import androidx.room.*
import com.switcherette.boarribs.data.Sighting

@Dao
interface SightingDao {

    @Query("SELECT * FROM Sighting ORDER BY timestamp DESC")
    fun getAll(): List<Sighting>

    @Query("SELECT * FROM Sighting WHERE id LIKE (:id)")
    suspend fun getOne(id: Int): Sighting

    //not used
    @Query("SELECT * FROM Sighting WHERE timestamp BETWEEN (:startDate) AND (:endDate)")
    suspend fun filterByDate(startDate: Long, endDate:Long): List<Sighting>

    //not used
    @Insert
    suspend fun insert(sighting: Sighting)

    //not used
    @Update(entity = Sighting::class)
    suspend fun update(sighting: Sighting)

    //not used
    @Delete
    suspend fun delete(sighting: Sighting)
}