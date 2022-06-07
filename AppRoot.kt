interface AppRoot : Rib, Connectable<Input, Output> {

    interface Dependency {
        val datasource: SightingsDatasource
    }

    sealed class Input {
        data class ShowSightingDetails(val id: String) : Input()
    }

    sealed class Output {}

}

interface AppRootView : RibView { 

    interface Factory : ViewFactory<AppRootView>
    
}


class AppRootViewImpl private constructor(
    override val androidView: ViewGroup,
) : AndroidRibView() {

    private val childContainer: FrameLayout by lazy { findViewById(R.id.childContainer) }
    private val permanentContainer: FrameLayout by lazy { findViewById(R.id.permanentContainer) }

    override fun getParentViewForSubtree(subtreeOf: Node<*>): ViewGroup =
    when (subtreeOf) {
        is NavBar -> permanentContainer
        else -> childContainer
    }

    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_app_root
    ) : AppRootView.Factory {

        override fun invoke(context: ViewFactory.Context): AppRootView =
            AppRootViewImpl(
                context.inflate(layoutRes)
            )
    }
}

internal class AppRootInteractor(
    buildParams: BuildParams<*>,
    private val backStack: BackStack<Configuration>,
    private val sightingDetails: SightingDetailsRibDialog,
) : Interactor<AppRoot, AppRootView>(
    buildParams = buildParams
) {

    private lateinit var dialogLauncher: DialogLauncher

    override fun onCreate(nodeLifecycle: Lifecycle) {
        nodeLifecycle.createDestroy {
            dialogLauncher = node.integrationPoint.dialogLauncher
        }

        whenChildBuilt<NavBar>(nodeLifecycle) { commonLifecycle, child ->
            commonLifecycle.createDestroy {
                bind(child.output to navBarOutputConsumer)
            }
        }
        whenChildBuilt<AllSightingsList>(nodeLifecycle) { commonLifecycle, child ->
            commonLifecycle.createDestroy {
                bind(child.output to allSightingsListOUtputConsumer)
            }
        }
        whenChildBuilt<NewSightingMap>(nodeLifecycle) { commonLifecycle, child ->
            commonLifecycle.createDestroy {
                bind(child.output to newSightingMapOutputConsumer)
            }
        }
         whenChildBuilt<NewSightingForm>(nodeLifecycle) { commonLifecycle, child ->
            commonLifecycle.createDestroy {
                bind(child.output to newSightingFormOutputConsumer)
            }
        }
    }

        override fun onViewCreated(view: AppRootView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            bind(dialog.rx2() to dialogEventConsumer)
        }
    }


    private val navBarOutputConsumer: Consumer<NavBar.Output> = Consumer {
        when (it) {
            NavBar.Output.MapButtonClicked -> backStack.push(Configuration.Content.AllSightingsMap)
            NavBar.Output.ListButtonClicked -> backStack.push(Configuration.Content.AllSightingsList)
            NavBar.Output.AddSightingButtonClicked -> backStack.push(Configuration.Content.NewSightingMap)
        }
    }

    private val allSightingsListOutputConsumer: Consumer<AllSightingsList.Output> = Consumer {
        when (it) {
            AllSightingsList.Output.SightingSelected -> {
                initDialog()
                backStack.push(Configuration.Overlay.SightingDetails(it.id))
                }
        }
    }

    private val allSightingsMapOutputConsumer: Consumer<AllSightingsMap.Output> = Consumer {
        when (it) {
            AllSightingsMap.Output.SightingSelected -> {
                initDialog()
                backStack.push(Configuration.Overlay.SightingDetails(it.id))
                }
        }
    }

    private fun initDialog() {
        dialogLauncher.show(sightingDetails) { dialogLauncher.hide(sightingDetails) }
    }


    private val dialogEventConsumer: Consumer<SightingDetailsRibDialog.Event> = Consumer {
        when (it) {
            SightingDetailsRibDialog.Event.NegativeButtonClicked -> {
                dialogLauncher.hide(dialog)
            }
            else -> {}
        }
    }

    private val newSightingMapOutputConsumer: Consumer<NewSightingMap.Output> = Consumer {
        when (it) {
            NewSightingMap.Output.LocationAdded -> backStack.push(Configuration.Content.NewSightingForm)
        }
    }

    private val newSightingFormOutputConsumer: Consumer<NewSightingForm.Output> = Consumer {
        when (it) {
            NewSightingForm.Output.SightingAdded -> backStack.push(Configuration.Content.AllSightingsList)
        }
    }


}

internal class AppRootRouter(
    buildParams: BuildParams<Nothing?>,
    routingSource: RoutingSource<Configuration>,
    private val builders: AppRootChildBuilders,
    transitionHandler: TransitionHandler<Configuration>
) : Router<Configuration>(
    buildParams = buildParams,
    routingSource = routingSource + RoutingSource.permanent(NavBar),
    transitionHandler = transitionHandler
) {

    sealed class Configuration : Parcelable {

        sealed class Permanent : Configuration() {
            @Parcelize
            object NavBar: Configuration()
        }

        sealed class Content : Configuration() {
            @Parcelize
            object NewSightingMap : Configuration()
            @Parcelize
            object NewSightingForm : Configuration()
            @Parcelize
            object AllSightingsMap : Configuration()
            @Parcelize
            object AllSightingsList : Configuration()
        }

        sealed class Overlay : Configuration() {
            @Parcelize
            data class SightingDetails(val id: String): Overlay()
        }
        
    }

    override fun resolve(routing: Routing<Configuration>): Resolution =
        with(builders) {
            when (routing.configuration) {
                NewSightingMap -> child { newSightingMapBuilder.build(it) }
                NewSightingForm -> child { newSightingFormBuilder.build(it) }
                AllSightingsMap -> child { allSightingsMapBuilder.build(it) }
                AllSightingsList -> child { allSightingsListBuilder.build(it) }
                NavBar -> child { navBarBuilder.build(it) }
                SightingDetails -> showDialog(
                        routingSource,          
                        routing.identifier,     
                        dialogLauncher,         
                        SightingDetailsRibDialog              
                    )
            }
        }
}


internal class AppRootChildBuilders( 
    dependency: AppRoot.Dependency 
) {

    private val subtreeDependency: SubtreeDependency = SubtreeDependency(dependency)

    val navBarBuilder = NavbarBuilder(subtreeDependency)
    val newSightingMapBuilder = NewSightingMapBuilder(subtreeDependency)
    val newSightingFormBuilder = NewSightingFormBuilder(subtreeDependency)
    val allSightingsMapBuilder = AllSightingsMapBuilder(subtreeDependency)
    val allSightingsListBuilder = AllSightingsListBuilder(subtreeDependency)
    val sightingDetailsBuilder = SightingDetailsBuilder(subtreeDependency)


    class SubtreeDependency(
        dependency: AppRoot.Dependency,
        override val rxNetwork: RxNetwork,
    ) : AppRoot.Dependency by dependency,
        NewSightingMap.Dependency,
        NewSightingForm.Dependency,
        AllSightingsMap.Dependency,
        AllSightingsList.Dependency,
        SightingDetails.Dependency
}



