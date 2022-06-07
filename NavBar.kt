interface NavBar : Rib, Connectable<Input, Output> {

    interface Dependency

    sealed class Input

    sealed class Output {
        object MapButtonClicked : Output()
        object ListButtonClicked : Output()
        object AddSightingButtonClicked : Output()
    }

    class Customisation(
        val viewFactory: NavBarView.Factory = NavBarViewImpl.Factory()
    ) : RibCustomisation

}


interface NavBarView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event{
        object MapButtonClicked : Event()
        object ListButtonClicked : Event()
        object AddNewSightingButtonClicked : Event()
    }

    fun interface Factory : ViewFactoryBuilder<Nothing?, NavBarView>
}


class NavBarViewImpl private constructor(
    override val androidView: ViewGroup,
    private val events: PublishRelay<Event> = PublishRelay.create()
) : AndroidRibView(),
    NavBarView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    private val mapButton: AppCompatButton by lazy { findViewById(R.id.mapButton) }
    private val listButton: AppCompatButton by lazy { findViewById(R.id.listButton) }
    private val addNewSightingButton: AppCompatButton by lazy { findViewById(R.id.addNewSightingButton) }

    init {
        setListeners()
    }

    private fun setListeners() {
        mapButton.setOnClickListener { events.accept(Event.MapButtonClicked) }
        listButton.setOnClickListener { events.accept(Event.ListButtonClicked) }
        addNewSightingConverterButton.setOnClickListener { events.accept(Event.AddNewSightingButtonClicked) }
    }

    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_nav_bar
    ) : NavBarView.Factory {
        override fun invoke(deps: Nothing?): ViewFactory<NavBarView> =
            ViewFactory {
                NavBarViewImpl(
                    it.inflate(layoutRes)
                )
            }
    }

}

internal class NavBarInteractor(
    buildParams: BuildParams<*>,
) : Interactor<NavBar, NavBarView>(
    buildParams = buildParams
) {

    override fun onViewCreated(view: NavBarView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            bind(view to rib.output using ViewEventToOutput)
        }
    }

}

internal object ViewEventToOutput : (Event) -> NavBar.Output? {

    override fun invoke(event: Event): NavBar.Output? = when (event){
        Event.MapButtonClicked -> NavBar.Output.MapButtonClicked
        Event.ListButtonClicked -> NavBar.Output.ListButtonClicked
        Event.AddNewSightingButtonClicked -> NavBar.Output.AddNewSightingButtonClicked
    }

}
