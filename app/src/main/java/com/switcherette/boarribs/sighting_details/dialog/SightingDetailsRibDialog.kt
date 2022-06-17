package com.switcherette.boarribs.sighting_details.dialog

import com.badoo.ribs.android.dialog.Dialog
import com.badoo.ribs.android.dialog.Dialog.CancellationPolicy.Cancellable
import com.badoo.ribs.android.dialog.Dialog.Event.Negative
import com.badoo.ribs.android.text.Text
import com.switcherette.boarribs.R
import com.switcherette.boarribs.sighting_details.SightingDetails
import com.switcherette.boarribs.sighting_details.SightingDetailsBuilder


class SightingDetailsRibDialog(
    sightingDetailsBuilder: SightingDetailsBuilder,
    sightingId: String
) : Dialog<Dialog.Event>(
    {
    title = Text.Resource(R.string.sighting_details)
    ribFactory {
        sightingDetailsBuilder.build(it, SightingDetails.BuildParams(sightingId))
    }
    buttons {
        positive(Text.Plain("OK"), Event.Positive)
        negative(Text.Plain("Back"), Event.Negative)
        neutral(Text.Plain("Neutral"), Event.Neutral)
    }

    cancellationPolicy = Cancellable(
        event = Event.Cancelled,
        cancelOnTouchOutside = true
    )

})


