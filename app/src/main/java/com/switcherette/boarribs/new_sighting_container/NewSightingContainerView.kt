package com.switcherette.boarribs.new_sighting_container

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.switcherette.boarribs.R

interface NewSightingContainerView : RibView {
    interface Factory : ViewFactory<NewSightingContainerView>
}


class NewSightingContainerViewImpl private constructor(
    override val androidView: ViewGroup,
) : AndroidRibView(),
    NewSightingContainerView {

    private val childContainer: FrameLayout by lazy { findViewById(R.id.childContainer) }

    override fun getParentViewForSubtree(subtreeOf: Node<*>): ViewGroup =
        childContainer

    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_new_sighting_container,
    ) : NewSightingContainerView.Factory {
        override fun invoke(context: ViewFactory.Context): NewSightingContainerView =
            NewSightingContainerViewImpl(
                context.inflate(layoutRes)
            )
    }

}
