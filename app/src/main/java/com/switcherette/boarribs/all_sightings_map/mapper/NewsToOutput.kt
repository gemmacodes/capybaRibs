package com.switcherette.boarribs.all_sightings_map.mapper

import com.switcherette.boarribs.all_sightings_map.AllSightingsMap
import com.switcherette.boarribs.all_sightings_map.feature.AllSightingsMapFeature


internal object NewsToOutput : (AllSightingsMapFeature.News) -> AllSightingsMap.Output? {

    override fun invoke(news: AllSightingsMapFeature.News): AllSightingsMap.Output? =
        when (news) {
            is AllSightingsMapFeature.News.LocationSaved -> AllSightingsMap.Output.LocationAdded(news.coordinates)
            is AllSightingsMapFeature.News.PermissionsRequired -> AllSightingsMap.Output.PermissionsRequired(news.permissions)
        }
}