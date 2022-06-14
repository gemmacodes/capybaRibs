package com.switcherette.boarribs.nav_bar

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatButton
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.ViewFactory
import com.switcherette.boarribs.nav_bar.NavBarView.Event
import com.jakewharton.rxrelay2.PublishRelay
import com.switcherette.boarribs.R
import io.reactivex.ObservableSource

interface NavBarView : RibView,
    ObservableSource<Event>{

    sealed class Event{
        object MapButtonClicked : Event()
        object ListButtonClicked : Event()
        object AddNewSightingButtonClicked : Event()
    }

    fun interface Factory : ViewFactory<NavBarView>
}


class NavBarViewImpl private constructor(
    override val androidView: ViewGroup,
    private val events: PublishRelay<Event> = PublishRelay.create()
) : AndroidRibView(),
    NavBarView,
    ObservableSource<Event> by events
{

    private val mapButton: AppCompatButton by lazy { findViewById(R.id.mapButton) }
    private val listButton: AppCompatButton by lazy { findViewById(R.id.listButton) }
    private val addButton: AppCompatButton by lazy { findViewById(R.id.addButton) }

    init {
        setListeners()
    }

    private fun setListeners() {
        mapButton.setOnClickListener { events.accept(Event.MapButtonClicked) }
        listButton.setOnClickListener { events.accept(Event.ListButtonClicked) }
        addButton.setOnClickListener { events.accept(Event.AddNewSightingButtonClicked) }
    }

    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_nav_bar
    ) : NavBarView.Factory {
        override fun invoke(context: ViewFactory.Context): NavBarView =
            NavBarViewImpl(
                context.inflate(layoutRes)
            )
        }

}
