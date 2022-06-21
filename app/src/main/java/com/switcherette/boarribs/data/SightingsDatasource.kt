package com.switcherette.boarribs.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.reactivex.Single
import kotlinx.parcelize.Parcelize


interface SightingsDataSource {
    fun loadSighting(id: String): Single<Sighting>
    fun loadSightings(
        //page: Int,
        //pageSize: Int,
        //northwest: Coordinates,
        //southeast: Coordinates
    ): Single<List<Sighting>>

    fun saveSighting(sighting: Sighting)
}


@Parcelize
@Entity
data class Sighting(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "heading") val heading: String,
    @ColumnInfo(name = "adults") val adults: Int,
    @ColumnInfo(name = "pups") val pups: Int,
    @ColumnInfo(name = "interaction") val interaction: Boolean,
    @ColumnInfo(name = "comments") val comments: String,
    @Embedded(prefix = "coordinates") val coordinates: Coordinates,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "picture") val picture: String,
) : Parcelable

@Parcelize
data class Coordinates(
    val latitude: Double,
    val longitude: Double,
) : Parcelable


internal object SightingsDataSourceImpl : SightingsDataSource {
    private val items = mutableMapOf<String, Sighting>()
    override fun loadSighting(id: String): Single<Sighting> = Single.just(items[id])

    override fun loadSightings(
        //page: Int,
        //pageSize: Int,
        //northwest: Coordinates,
        //southeast: Coordinates
    ): Single<List<Sighting>> =
        Single.just(items.values.toList())

    override fun saveSighting(sighting: Sighting) {
        items[sighting.id] = sighting
    }

}