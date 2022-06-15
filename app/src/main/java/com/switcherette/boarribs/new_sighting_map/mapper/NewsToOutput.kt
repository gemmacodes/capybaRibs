package com.switcherette.boarribs.new_sighting_map.mapper

import com.switcherette.boarribs.new_sighting_map.NewSightingMap.Output
import com.switcherette.boarribs.new_sighting_map.feature.NewSightingMapFeature.News

internal object NewsToOutput : (News) -> Output? {

    override fun invoke(news: News): Output? =
        when (news) {
            is News.LocationSaved -> Output.LocationAdded(news.coordinates.longitude, news.coordinates.latitude)
            else -> null
        }
}
