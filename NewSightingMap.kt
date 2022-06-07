interface NewSightingMap : Rib, Connectable<Input, Output> {

    interface Dependency {
    }

    sealed class Input {
    }

    sealed class Output{
        data class LocationAdded (val longitude: Int, val latitude: Int): Output()
    }

}

interface NewSightingMapView : RibView, ObservableSource<Event> {

    sealed class Event {
        data class SaveLocation(val longitude: Int, val latitude: Int) : Event()
    }

    sealed class ViewModel {
    data class Content(
        val longitude: Int,
        val latitude: Int
    ) : ViewModel()
    }

    interface Factory : ViewFactory<NewSightingMapView>
}

class NewSightingMapViewImpl private constructor(
    override val androidView: ViewGroup,
    private val events: PublishRelay<Event> = PublishRelay.create()
) : AndroidRibView(),
    NewSightingMapView,
    ObservableSource<Event> by events {

    private val mapView: MapView by lazy { findViewById(R.id.mapView) }
    private val nextBtn: Button by lazy { findViewById(R.id.nextBtn) }

    init {
        nextBtn.setOnClickListener {
            events.accept(Event.SaveLocation(mapView.latitude, mapView.longitude)
        }
    }


    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_add_sighting_map
    ) : NewSightingMapView.Factory {

        override fun invoke(context: ViewFactory.Context): NewSightingMapView =
            NewSightingMapViewImpl(
                context.inflate(layoutRes)
            )
    }

    override fun accept(vm: ViewModel) {
    bind(vm)
    }
}

internal class NewSightingMapInteractor(
    buildParams: BuildParams<*>,
    private val dialog: SightingDetailsRibDialog,
) : Interactor<NewSightingMap, NewSightingMapView>(
    buildParams = buildParams
) {


    override fun onViewCreated(view: NewSightingMapView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            bind(feature to view using StateToViewModel)
            bind(view to feature using ViewEventToWish)
            bind(feature.news to NewSightingForm.input using NewsToNewSightingFormInput)
        }
    }

}

internal object ViewEventToWish : (Event) -> Wish? {

    override fun invoke(event: Event): Output? =
        when (event) {
            is Event.SaveLocation -> Wish.SaveLocation(it.latitude, it.longitude)
            else -> null
        }
}

internal object NewsToNewSightingFormInput : (News) -> Input? {

    override fun invoke(news: News): Input? = when (news) {
        News.LocationSaved -> Input.CoordinatesAdded(news.latitude, news.longitude)
        else -> null
    }
}



internal class NewSightingMapFeature() : BaseFeature<Wish, Action, Effect, State, News>(
    initialState = State(),
    wishToAction = Action::ExecuteWish,
    actor = ActorImpl(
        sightingsDatasource = sightingsDatasource
    ),
    reducer = ReducerImpl(),
    newsPublisher = NewsPublisherImpl(),
) {

    @Parcelize
    data class State(
        val longitude: Int = 0,
        val longitude: Int = 0,
    ) : Parcelable

    sealed class Wish {
        data class SaveLocation(val longitude: Int, val latitude: Int) : Action()
    }

    sealed class Action {
        data class ExecuteWish(val wish: Wish) : Action()
    }

    sealed class Effect {
        data class LocationSaved(val longitude: Int, val latitude: Int) : Effect()
    }

    sealed class News {
        data class LocationSaved(val longitude: Int, val latitude: Int) : News()
    }


    class ActorImpl(
        private val sightingsDatasource: SightingsDatasource
    ) : Actor<State, Action, Effect> {

        override fun invoke(state: State, action: Action): Observable<out Effect> =
            when (action) {
                is SaveLocation -> Observable.just(Effect.LocationSaved(action.longitude, action.latitude))
        }

    }

    class ReducerImpl : Reducer<State, Effect> {

        override fun invoke(state: State, effect: Effect): State =
            when (effect) {
                is LocationSaved -> state.copy(
                    longitude = effect.longitude,
                    latitude = effect.latitude
                )
            }
    }

    class NewsPublisherImpl : NewsPublisher<Wish, Effect, State, News> {
        override fun invoke(wish: Wish, effect: Effect, state: State): News? =
            when (effect) {
                is Effect.LocationSaved -> News.LocationSaved(effect.longitude, effect.latitude)
                else -> null
            }
    }

}