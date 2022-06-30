package com.switcherette.boarribs.camera

import com.badoo.ribs.android.activitystarter.ActivityStarter
import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.rx2.disposables
import com.switcherette.boarribs.camera.feature.CameraFeature

class CameraBuilder(
    private val dependency: Camera.Dependency
) : SimpleBuilder<Camera>() {

    override fun build(buildParams: BuildParams<Nothing?>): Camera {
        val customisation = buildParams.getOrDefault(Camera.Customisation())
        val activityStarter = dependency.activityStarter
        val feature = feature()
        val interactor = interactor(buildParams, feature, activityStarter)

        return node(buildParams, customisation, feature, interactor)
    }

    private fun feature() =
        CameraFeature()

    private fun interactor(
        buildParams: BuildParams<*>,
        feature: CameraFeature,
        activityStarter: ActivityStarter
    ) = CameraInteractor(
            buildParams = buildParams,
            feature = feature,
            activityStarter = activityStarter
        )

    private fun node(
        buildParams: BuildParams<Nothing?>,
        customisation: Camera.Customisation,
        feature: CameraFeature,
        interactor: CameraInteractor
    ) = CameraNode(
            buildParams = buildParams,
            viewFactory = customisation.viewFactory,
            plugins = listOf(interactor, disposables(feature))
        )
}
