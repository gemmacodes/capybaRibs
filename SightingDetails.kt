interface SightingDetails : Rib, Connectable<Input, Output> {

    interface Dependency {
    }

    sealed class Input {
    }

    sealed class Output

}


interface SightingsDetailsView : RibView {

    sealed class ViewModel {
        data class Content(
            val sighting: Sighting,
        ) : ViewModel()
    }

    interface Factory : ViewFactory<SightingsDetailsView>
}


class SightingsDetailsViewImpl(
    override val androidView: ViewGroup
) : AndroidRibView(),
    SightingsDetailsView {

    private val title: TextView by lazy { findViewById(R.id.tvTitle) }
    private val adults: TextView by lazy { findViewById(R.id.tvAdults) }
    private val piglets: TextView by lazy { findViewById(R.id.tvPiglets) }
    private val interacting: TextView by lazy { findViewById(R.id.tvInteracting) }
    private val location: TextView by lazy { findViewById(R.id.tvLocation) }
    private val description: TextView by lazy { findViewById(R.id.tvDescription) }
    private val photo: ImageView by lazy { findViewById(R.id.ivPhoto) }


    private fun bind(vm: ViewModel) {
        when (vm) {
            is ViewModel.Content -> {
                title.text = vm.sighting.title.toString()
                adults.text = vm.sighting.adults.toString()
                piglets.text = vm.sighting.piglets.toString()
                interacting.text = vm.sighting.interacting.toString()
                location.text = vm.sighting.location.toString()
                description.text = vm.sighting.description.toString()
                photo.setImageResource(vm.photo)
            }
        }
    }


    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_sightings_details,
    ) : ViewFactoryBuilder<SightingsDetailsView.ViewDependency, SightingsDetailsView> {

        override fun invoke(viewDependency: SightingsDetailsView.ViewDependency): ViewFactory<SightingsDetailsView> =
            ViewFactory {
                SightingsDetailsViewImplA(
                    androidView = it.inflate(layoutRes)
                )
            }

    }

    override fun accept(vm: ViewModel) {
        bind(vm)
    }

}


internal class SightingsDetailsFeature(
    sightingsDatasource: SightingsDatasource,
    initialId: String
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
        val sighting: Sighting? = null,
    ) : Parcelable

    sealed class Wish {
    }

    sealed class Action {
        data class ExecuteWish(val wish: Wish) : Action()
        data class DisplaySighting(val id: String) : Action()
    }

    sealed class Effect {
        data class SightingDisplayed(val sighting : Sighting) : Effect()
    }

    class BootStrapperImpl : Bootstrapper<Action> {

        override fun invoke(): Observable<Action> = init()

        private fun init(): Observable<Action> =
            Observable.just(DisplaySighting(initialId))
    }

    class ActorImpl(
        private val sightingsDatasource: SightingsDatasource
    ) : Actor<State, Action, Effect> {

        override fun invoke(state: State, action: Action): Observable<out Effect> =
            when (action) {
                is DisplaySightings -> retrieveSighting(action.id)
            }

        private fun retrieveSightings(id: String): Observable<Effect> =
            state.sighting
                ?.let { Observable.empty() }
                ?: SightingsDatasource
                    .getSighting(id) //TODO
                    .toObservable()
                    .map<Effect> { SightingDisplayed(it) }
    }

    class ReducerImpl : Reducer<State, Effect> {

        override fun invoke(state: State, effect: Effect): State =
            when (effect) {
                is SightingDisplayed -> state.copy(
                    sighting = effect.sighting
                )
            }
    }
}



class SightingDetailsRibDialog(
    sightingDetailsBuilder: SightingDetailsBuilder
) : Dialog<Dialog.Event>({
    title = Text.Resource(R.string.sighting_details)
    ribFactory {
        sightingDetailsBuilder.build(it)
    }
    buttons {
        negative(Text.Plain("Back"), Negative)
    }
})

