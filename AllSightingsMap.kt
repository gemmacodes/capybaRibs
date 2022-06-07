interface AllSightingsMap : Rib, Connectable<Input, Output> {

    interface Dependency {
    }

    sealed class Input {
    }

    sealed class Output{
        data class SightingSelected(val id: String) : Output()
    }

}

interface AllSightingsMapView : RibView, ObservableSource<Event> {

    sealed class Event {
        data class ShowSightingDetails (val id: String) : Event()
    }

    sealed class ViewModel {
    data class Content(
        val sightings: list<Sighting>
        val zoom: Int
    ) : ViewModel()
    }

    interface Factory : ViewFactory<AllSightingsMapView>
}

class AllSightingsMapViewImpl private constructor(
    override val androidView: ViewGroup,
    private val events: PublishRelay<Event> = PublishRelay.create()
) : AndroidRibView(),
    AllSightingsMapView,
    ObservableSource<Event> by events {

    private val sightingsMap: MapView by lazy { findViewById(R.id.sightingsMap) }



    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_all_sightings_list
    ) : AllSightingsMapView.Factory {

        override fun invoke(context: ViewFactory.Context): AllSightingsMapView =
            AllSightingsMapViewImpl(
                context.inflate(layoutRes)
            )
    }

    override fun accept(vm: ViewModel) {
    bind(vm)
    }

    fun bind(vm: ViewModel){
        sightingsMap.getMapboxMap().setCamera(
        CameraOptions.Builder()
            .zoom(vm.zoom)
            .build()
        )
        sightingsMap.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            addBoarAnnotations(vm.sightings)
        }
    }

    private fun addBoarAnnotations(sightings: list<Sighting>?) {
        val annotationApi = mapView.annotations
        val pointAnnotationManager = annotationApi.createPointAnnotationManager()
        sightings { sighting ->
            sighting.forEach {
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(it.longitude, it.latitude))
                    .withIconImage(
                        bitmapFromDrawableRes(
                            requireContext(),
                            R.drawable.boar
                        )!!
                    )

                pointAnnotationManager.addClickListener(onPointAnnotationClickListener)
                pointAnnotationManager.create(pointAnnotationOptions)
            }
        }
    }

    open override fun addClickListener(u: OnPointAnnotationClickListener): Boolean {
        event.accept(Event.ShowSightingDetails(u.getId()))
    }
}

internal class AllSightingsMapInteractor(
    buildParams: BuildParams<*>,
    private val dialog: SightingDetailsRibDialog,
) : Interactor<AllSightingsMap, AllSightingsMapView>(
    buildParams = buildParams
) {

    private lateinit var dialogLauncher: DialogLauncher

    override fun onCreate(nodeLifecycle: Lifecycle) {
        super.onCreate(nodeLifecycle)
        nodeLifecycle.createDestroy {
            dialogLauncher = node.integrationPoint.dialogLauncher
        }
    }

    override fun onViewCreated(view: AllSightingsMapView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            bind(view to rib.output using ViewEventToOutput)
            bind(dialog.rx2() to dialogEventConsumer)
            bind(feature to view using StateToViewModel)
            bind(view to feature using ViewEventToWish)
        }
    }



}

internal object ViewEventToOutput : (Event) -> Output? {

    override fun invoke(event: Event): Output? =
        when (event) {
            is ShowSightingDetails -> Output.ShowSightingDetails(it.id)
            else -> null
        }
}


internal class AllSightingsMapFeature(
    sightingsDatasource: SightingsDatasource,
) : BaseFeature<Wish, Action, Effect, State, Nothing>(
    initialState = State(),
    bootstrapper = BootStrapperImpl(),
    wishToAction = Action::ExecuteWish,
    actor = ActorImpl(
        sightingsDatasource = sightingsDatasource
    ),
    reducer = ReducerImpl()
) {

    @Parcelize
    data class State(
        val sightings: list<Sighting>? = null,
    ) : Parcelable

    sealed class Wish {
    }

    sealed class Action {
        data class ExecuteWish(val wish: Wish) : Action()
        object DisplaySightings : Action()
    }

    sealed class Effect {
        data class SightingDisplayed(val id : String) : Effect()
    }

    class BootStrapperImpl : Bootstrapper<Action> {

        override fun invoke(): Observable<Action> = init()

        private fun init(): Observable<Action> =
            Observable.just(DisplaySightings)
    }

    class ActorImpl(
        private val sightingsDatasource: SightingsDatasource
    ) : Actor<State, Action, Effect> {

        override fun invoke(state: State, action: Action): Observable<out Effect> =
            when (action) {
                is DisplaySightings -> retrieveSightings(state = state)
            }

        private fun retrieveSightings(state: State): Observable<Effect> =
            state.sightings
                ?.let { Observable.empty() }
                ?: SightingsDatasource
                    .getAllSightings() //TODO
                    .map<Effect> { SightingDisplayed(id = it.id) }
                    .toObservable()
    }

    class ReducerImpl : Reducer<State, Effect> {

        override fun invoke(state: State, effect: Effect): State =
            val sightingsMutableList = mutableListOf<Sighting>()
            when (effect) {
                is SightingDisplayed -> state.copy(
                    sightings = sightingsMutableList.apply { add(it) }
                )
            }
    }
}