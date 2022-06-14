package com.switcherette.boarribs.new_sighting_form.mapper

import android.widget.Toast
import com.switcherette.boarribs.new_sighting_form.NewSightingForm.Output
import com.switcherette.boarribs.new_sighting_form.feature.NewSightingFormFeature

internal object NewsToOutput : (NewSightingFormFeature.News) -> Output? {

    override fun invoke(news: NewSightingFormFeature.News): Output? =
        when (news) {
            NewSightingFormFeature.News.SightingNotSaved -> null
            NewSightingFormFeature.News.SightingSaved -> Output.SightingAdded
        }
}


