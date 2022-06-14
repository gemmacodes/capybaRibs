package com.switcherette.boarribs.new_sighting_form

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.jakewharton.rxrelay2.PublishRelay
import com.switcherette.boarribs.R
import com.switcherette.boarribs.new_sighting_form.NewSightingFormView.Event
import com.switcherette.boarribs.new_sighting_form.NewSightingFormView.ViewModel
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface NewSightingFormView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event {
        data class SaveSighting(
            val heading: String?,
            val adults: Int?,
            val piglets: Int?,
            val interaction: Boolean,
            val comments: String?,
            val picture: String?
        ): Event()

        object TakePicture: Event()

    }

    data class ViewModel(
        val heading: String?,
        val adults: Int?,
        val piglets: Int?,
        val interaction: Boolean,
        val comments: String?,
        val picture: String?
    )

    fun interface Factory : ViewFactory<NewSightingFormView>
}


class NewSightingFormViewImpl private constructor(
    override val androidView: ViewGroup,
    private val events: PublishRelay<Event> = PublishRelay.create()
) : AndroidRibView(),
    NewSightingFormView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_new_sighting_form
    ) : NewSightingFormView.Factory {
        override fun invoke(context: ViewFactory.Context): NewSightingFormView =
            NewSightingFormViewImpl(
                context.inflate(layoutRes)
            )
    }

    override fun accept(vm: ViewModel) {
    }
}
