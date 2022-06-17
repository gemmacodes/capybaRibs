package com.switcherette.boarribs.sighting_details.dialog

import com.badoo.ribs.android.dialog.Dialog
import com.badoo.ribs.android.dialog.Dialog.CancellationPolicy.Cancellable
import com.badoo.ribs.android.text.Text
import com.switcherette.boarribs.sighting_details.SightingDetails
import com.switcherette.boarribs.sighting_details.SightingDetailsBuilder


class SightingDetailsRibDialog(
    sightingDetailsBuilder: SightingDetailsBuilder,
    sightingId: String,
) : Dialog<Dialog.Event>(
    {
        ribFactory {
            sightingDetailsBuilder.build(it, SightingDetails.BuildParams(sightingId))
        }
        buttons {
            negative(Text.Plain("Back"), Event.Negative)
        }
        cancellationPolicy = Cancellable(
            event = Event.Cancelled,
            cancelOnTouchOutside = true
        )
    })
