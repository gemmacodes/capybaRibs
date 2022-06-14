package com.switcherette.boarribs.app_root

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.ViewFactory
import com.switcherette.boarribs.R
import com.switcherette.boarribs.nav_bar.NavBar

interface AppRootView : RibView {

    interface Factory : ViewFactory<AppRootView>
}


class AppRootViewImpl private constructor(
    override val androidView: ViewGroup
) : AndroidRibView(),
    AppRootView {

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
