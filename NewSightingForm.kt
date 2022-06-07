interface NewSightingForm : Rib, Connectable<Input, Output> {

    interface Dependency {
        val sightingsDatasource: SightingsDatasource
    }

    sealed class Input {
        object CoordinatesAdded (val longitude: Int, val latitude: Int) : Input()
    }

    sealed class Output{
        object SightingAdded : Output()
    }

}

interface NewSightingFormView : RibView, ObservableSource<Event> {

    sealed class Event {
        data class SaveSighting(val sighting: Sighting) : Event()
    }

    sealed class ViewModel {
    data class Content(
        val sighting: Sighting
    ) : ViewModel()
    }

    interface Factory : ViewFactory<NewSightingFormView>
}

class NewSightingFormViewImpl private constructor(
    override val androidView: ViewGroup,
    private val events: PublishRelay<Event> = PublishRelay.create()
) : AndroidRibView(),
    NewSightingFormView,
    ObservableSource<Event> by events {

    private val title: EditText by lazy { findViewById(R.id.tvTitle) }
    private val adults: EditText by lazy { findViewById(R.id.tvAdults) }
    private val piglets: EditText by lazy { findViewById(R.id.tvPiglets) }
    private val interacting: EditText by lazy { findViewById(R.id.tvInteracting) }
    private val description: EditText by lazy { findViewById(R.id.tvDescription) }
    private val photo: ImageView by lazy { findViewById(R.id.ivPhoto) }
    private val save: Button by lazy { findViewById(R.id.btnSave) }

    init {
        save.setOnClickListener {
            events.accept(Event.SaveSighting(Sighting(
                title = title.text.toString(),
                adults = adults.text.toString().toInt(),
                piglets = piglets.text.toString().toInt(),
                interacting = interacting.text.toString(),
                location = null,
                description = description.text.toString(),
                photo = photo.drawable.toString()
            )))
        }
    }


    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_add_sighting_form
    ) : NewSightingFormView.Factory {

        override fun invoke(context: ViewFactory.Context): NewSightingFormView =
            NewSightingFormViewImpl(
                context.inflate(layoutRes)
            )
    }

    override fun accept(vm: ViewModel) {
    bind(vm)
    }
}

internal class NewSightingFormInteractor(
    buildParams: BuildParams<*>,
    private val dialog: SightingDetailsRibDialog,
) : Interactor<NewSightingForm, NewSightingFormView>(
    buildParams = buildParams
) {

    override fun onCreate(nodeLifecycle: Lifecycle) {
        nodeLifecycle.createDestroy {
            bind(feature.news to rib.output using NewsToOutput)
            bind(rib.input to feature using InputToWish)
        }


    override fun onViewCreated(view: NewSightingFormView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            bind(feature to view using StateToViewModel)
            bind(view to feature using ViewEventToWish)
        }
    }

}

internal object ViewEventToWish : (Event) -> Wish? {

    override fun invoke(event: Event): Output? =
        when (event) {
            is SaveSighting -> Wish.SaveSighting(it.sighting)
            else -> null
        }
}

internal object InputToWish : (Input) -> Wish? {

    override fun invoke(news: Input): Wish? =
        when (news) {
            is NewSightingForm.Input.CoordinatesAdded -> Wish.CoordinatesAdded(news.longitude, news.latitude)
}


internal object NewsToOutput : (NewSightingFormFeature.News) -> NewSightingForm.Output? {

    override fun invoke(news: NewSightingFormFeature.News): NewSightingForm.Output? =
        when (news) {
            is NewSightingFormFeature.News.SightingAdded -> NewSightingForm.Output.SightingAdded
            else -> null
        }
}



internal class NewSightingFormFeature(
    sightingsDatasource: SightingsDatasource,
) : BaseFeature<Wish, Action, Effect, State, News>(
    initialState = State(),
    wishToAction = Action::ExecuteWish,
    actor = ActorImpl(
        sightingsDatasource = sightingsDatasource
    ),
    reducer = ReducerImpl(),
    newsPublisher = NewsPublisherImpl()
) {

    @Parcelize
    data class State(
        val sighting: Sighting? = null,
    ) : Parcelable

    sealed class Wish {
        data class AddCoordinates(val longitude: Int, val latitude: Int) : Wish()
        data class SaveSighting(val sighting: Sighting) : Wish()
    }

    sealed class Action {
        data class ExecuteWish(val wish: Wish) : Action()
    }

    sealed class Effect {
        data class SightingSaved(val sighting: Sighting) : Effect()
        data class CoordinatesAdded(val longitude: Int, val latitude: Int) : Effect()
    }

    sealed class News {
        object SightingAdded : News()
    }


    class ActorImpl(
        private val sightingsDatasource: SightingsDatasource
    ) : Actor<State, Action, Effect> {

        override fun invoke(state: State, action: Action): Observable<out Effect> =
            when (action) {
                is AddCoordinates -> Observable.just(Effect.CoordinatesAdded(action.longitude, action.latitude))
                is SaveSighting -> saveSighting(state = state)
            }


        private fun saveSighting(state: State): Observable<Effect> =
            state.sighting
                ?.let { Observable.empty() }
                ?: SightingsDatasource
                    .insertSighting(it) //TODO
                    .toObservable()
                    .map<Effect> { SightingSaved(sighting = it) }
    }

    class ReducerImpl : Reducer<State, Effect> {

        override fun invoke(state: State, effect: Effect): State =
            when (effect) {
                is CoordinatesAdded -> state.copy(
                    sighting.longitude = effect.longitude,
                    sighting.latitude = effect.latitude
                )
                is SightingSaved -> state.copy(
                    sighting.copy(
                        id = effect.sighting.id,
                        title = effect.sighting.title,
                        adults = effect.sighting.adults,
                        piglets = effect.sighting.piglets,
                        interacting = effect.sighting.interacting,
                        description = effect.sighting.description,
                        photo = effect.sighting.photo
                    )
                )
            }
    }

    class NewsPublisherImpl : NewsPublisher<Wish, Effect, State, News> {
        override fun invoke(wish: Wish, effect: Effect, state: State): News? =
            when (effect) {
                is Effect.SightingSaved -> News.SightingAdded
                else -> null
            }
    }

}