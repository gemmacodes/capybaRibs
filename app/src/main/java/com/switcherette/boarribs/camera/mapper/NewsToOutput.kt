package com.switcherette.boarribs.camera.mapper

import com.switcherette.boarribs.camera.Camera.Output
import com.switcherette.boarribs.camera.feature.CameraFeature.News
import com.switcherette.boarribs.new_sighting_form.NewSightingForm
import com.switcherette.boarribs.new_sighting_form.feature.NewSightingFormFeature

internal object NewsToOutput : (News) -> Output? {

    override fun invoke(news: News): Output? =
        when (news){
            is News.PermissionsRequired -> Output.PermissionsRequired(news.permissions)
            is News.PhotoTaken -> Output.PhotoTaken(news.filepath)
        }
}
