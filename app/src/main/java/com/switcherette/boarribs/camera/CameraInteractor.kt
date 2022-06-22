package com.switcherette.boarribs.camera

import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.badoo.binder.using
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.source.backstack.operation.replace
import com.mapbox.maps.MapView
import com.switcherette.boarribs.R
import com.switcherette.boarribs.camera.feature.CameraFeature
import com.switcherette.boarribs.camera.mapper.InputToWish
import com.switcherette.boarribs.camera.mapper.NewsToOutput
import com.switcherette.boarribs.camera.mapper.StateToViewModel
import com.switcherette.boarribs.camera.mapper.ViewEventToWish
import com.switcherette.boarribs.databinding.RibCameraBinding
import com.switcherette.boarribs.new_sighting_container.NewSightingContainerInteractor
import com.switcherette.boarribs.new_sighting_container.routing.NewSightingContainerRouter
import com.switcherette.boarribs.new_sighting_map.NewSightingMap
import io.reactivex.functions.Consumer

internal class CameraInteractor(
    buildParams: BuildParams<*>,
    private val feature: CameraFeature,
) : Interactor<Camera, CameraView>(
    buildParams = buildParams
) {

    override fun onCreate(nodeLifecycle: Lifecycle) {
        nodeLifecycle.createDestroy {
            bind(feature.news to rib.output using NewsToOutput)
            bind(rib.input to feature using InputToWish)
        }
    }

    override fun onViewCreated(view: CameraView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            bind(feature to view using StateToViewModel)
            bind(view to feature using ViewEventToWish)
        }

        viewLifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                feature.accept(CameraFeature.Wish.OpenCameraIfReady)

                //TODO: Does this go here?
                val imageCapture: ImageCapture? = null
                val binding = RibCameraBinding.bind(view.androidView)
                startCamera(view.context, imageCapture, binding.viewFinder.surfaceProvider , owner)
            }

            override fun onStop(owner: LifecycleOwner) {

            }
        })
    }

}
