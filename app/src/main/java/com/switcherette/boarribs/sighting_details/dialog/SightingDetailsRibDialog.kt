package com.switcherette.boarribs.sighting_details.dialog

import com.badoo.ribs.android.dialog.Dialog
import com.badoo.ribs.android.dialog.Dialog.CancellationPolicy.Cancellable
import com.badoo.ribs.android.dialog.Dialog.Event.Negative
import com.badoo.ribs.android.text.Text
import com.switcherette.boarribs.R
import com.switcherette.boarribs.app_root.routing.AppRootRouter
import com.switcherette.boarribs.data.Sighting
import com.switcherette.boarribs.sighting_details.SightingDetails
import com.switcherette.boarribs.sighting_details.SightingDetailsBuilder

class SightingDetailsRibDialog(
    sightingDetailsBuilder: SightingDetailsBuilder,
    sightingId: String
) : Dialog<Dialog.Event>({
    title = Text.Resource(R.string.sighting_details)
    ribFactory {
        sightingDetailsBuilder.build(it, SightingDetails.Params(sightingId))
    }
    buttons {
        negative(Text.Plain("Back"), Negative)
    }

    cancellationPolicy = Cancellable(
        event = Event.Cancelled,
        cancelOnTouchOutside = true
    )

})
