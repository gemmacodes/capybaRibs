package com.switcherette.boarribs.data.repositories

import com.switcherette.boarribs.data.Sighting
import com.switcherette.boarribs.data.database.SightingDao


class SightingRepository(private val sightingDao: SightingDao) {

    val allSightings: List<Sighting> = sightingDao.getAll()

    suspend fun insert(sighting: Sighting) {
        sightingDao.insert(sighting)
    }

    //not used
    suspend fun filterByDate(start: Long, end:Long):List<Sighting> {
        return sightingDao.filterByDate(start, end)
    }

    //not used
    suspend fun getSighting(id: Int) {
        sightingDao.getOne(id)
    }

    //not used
    suspend fun update(sighting: Sighting) {
        sightingDao.update(sighting)
    }

    //not used
    suspend fun delete(sighting: Sighting) {
        sightingDao.delete(sighting)
    }


}