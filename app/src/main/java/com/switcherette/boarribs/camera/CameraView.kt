package com.switcherette.boarribs.camera

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.jakewharton.rxrelay2.PublishRelay
import com.switcherette.boarribs.R
import com.switcherette.boarribs.camera.CameraView.Event
import com.switcherette.boarribs.camera.CameraView.ViewModel
import com.switcherette.boarribs.databinding.RibCameraBinding
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface CameraView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event{
        object PhotoCaptureRequested: Event()
    }

    data class ViewModel(
        val i: Int = 0,
    )

    fun interface Factory : ViewFactory<CameraView>
}


class CameraViewImpl private constructor(
    override val androidView: ViewGroup,
    private val events: PublishRelay<Event> = PublishRelay.create(),
) : AndroidRibView(),
    CameraView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    private val binding = RibCameraBinding.bind(androidView)

    init {
        binding.btnImageCapture.setOnClickListener { events.accept(Event.PhotoCaptureRequested) }
    }

    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_camera,
    ) : CameraView.Factory {
        override fun invoke(context: ViewFactory.Context): CameraView =
            CameraViewImpl(
                context.inflate(layoutRes))
    }


    override fun accept(vm: ViewModel) {
    }
}
