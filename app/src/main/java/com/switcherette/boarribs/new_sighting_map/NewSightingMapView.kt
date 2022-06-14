package com.switcherette.boarribs.new_sighting_map

import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.content.res.AppCompatResources
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.jakewharton.rxrelay2.PublishRelay
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.Annotation
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.switcherette.boarribs.R
import com.switcherette.boarribs.data.Sighting
import com.switcherette.boarribs.databinding.RibNewSightingMapBinding
import com.switcherette.boarribs.new_sighting_map.NewSightingMapView.Event
import com.switcherette.boarribs.new_sighting_map.NewSightingMapView.ViewModel
import com.switcherette.boarribs.utils.bitmapFromDrawableRes
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface NewSightingMapView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event {
        object GetGeolocation : Event()
        data class SetPointerLocation(val longitude: Double, val latitude: Double) : Event()
        data class SaveLocation(val longitude: Double, val latitude: Double) : Event()
    }

    sealed class ViewModel {
        data class Content(
            val longitude: Double,
            val latitude: Double
        ) : ViewModel()
    }

    fun interface Factory : ViewFactory<NewSightingMapView>
}


class NewSightingMapViewImpl private constructor(
    override val androidView: ViewGroup,
    private val events: PublishRelay<Event> = PublishRelay.create()
) : AndroidRibView(),
    NewSightingMapView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    private val binding: RibNewSightingMapBinding = RibNewSightingMapBinding.bind(androidView)

    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_new_sighting_map
    ) : NewSightingMapView.Factory {
        override fun invoke(context: ViewFactory.Context): NewSightingMapView =
            NewSightingMapViewImpl(
                context.inflate(layoutRes),
            )
    }


    override fun accept(vm: ViewModel) {
        when (vm) {
            is ViewModel.Content -> {
                val longitude = vm.longitude
                val latitude = vm.latitude
                with(binding) {
                    fabAddMap.setOnClickListener {
                        events.accept(Event.SaveLocation(longitude, latitude))
                    }
                    btnGeoLoc.setOnClickListener {
                        events.accept(Event.GetGeolocation) }
                    mapAdd.getMapboxMap().setCamera(
                        CameraOptions.Builder()
                            .zoom(14.0)
                            .center(Point.fromLngLat(longitude, latitude))
                            .build()
                    )
                    mapAdd.getMapboxMap().loadStyleUri(
                        Style.MAPBOX_STREETS
                    ) {
                        initLocationComponent()
                        setupGesturesListener()
                        addAnnotationToMap(binding.mapAdd.annotations, vm)
                    }
                }
            }
        }
    }

    private fun addAnnotationToMap(annotationApi: AnnotationPlugin, vm: ViewModel.Content) {
        bitmapFromDrawableRes(
            context,
            R.drawable.boar
        )?.let {
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(vm.longitude, vm.latitude))
                // Point.fromLngLat(2.111255, 41.409428)
                .withIconImage(it)
                .withDraggable(true)

            val boarPointAnnotationManager = annotationApi.createPointAnnotationManager()
            boarPointAnnotationManager.addDragListener(dragListener)
            boarPointAnnotationManager.create(pointAnnotationOptions)
        }
    }

    private val dragListener = object : OnPointAnnotationDragListener {
        override fun onAnnotationDrag(annotation: Annotation<*>) {}
        override fun onAnnotationDragStarted(annotation: Annotation<*>) {}
        override fun onAnnotationDragFinished(annotation: Annotation<*>) {
            events.accept(
                Event.SetPointerLocation(
                    (annotation as PointAnnotation).point.longitude(),
                    annotation.point.latitude()
                )
            )
        }
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = binding.mapAdd.location

        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.pulsingEnabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    context,
                    com.mapbox.maps.plugin.locationcomponent.R.drawable.mapbox_user_icon,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    context,
                    com.mapbox.maps.plugin.locationcomponent.R.drawable.mapbox_user_icon_shadow,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        // Pass the user's location to camera
        locationComponentPlugin.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
    }

    private fun setupGesturesListener() {
        binding.mapAdd.gestures.addOnMoveListener(onMoveListener)
        binding.mapAdd.gestures.addOnMapClickListener { point ->
            binding.mapAdd.location
                .isLocatedAt(point) { isPuckLocatedAtPoint ->
                    if (isPuckLocatedAtPoint) {
                        Toast.makeText(
                            context,
                            "Clicked on location puck ${binding.mapAdd.location}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            true
        }
        binding.mapAdd.gestures.addOnMapLongClickListener { point ->
            binding.mapAdd.location
                .isLocatedAt(point) { isPuckLocatedAtPoint ->
                    if (isPuckLocatedAtPoint) {
                        Toast.makeText(
                            context,
                            "Long-clicked on location puck ${binding.mapAdd.location}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            true
        }
    }

    // CAMERA TRACKING
    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        // Jump to the current indicator position
        binding.mapAdd.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        // Set the gestures plugin's focal point to the current indicator location.
        binding.mapAdd.gestures.focalPoint = binding.mapAdd.getMapboxMap().pixelForCoordinate(it)
        events.accept(Event.SetPointerLocation(it.longitude(), it.latitude()))
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }
        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }
        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    private fun onCameraTrackingDismissed() {
        binding.mapAdd.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        binding.mapAdd.gestures.removeOnMoveListener(onMoveListener)
    }

}




