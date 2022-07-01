package com.switcherette.boarribs.camera

import com.badoo.ribs.android.activitystarter.CanProvideActivityStarter
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisation
import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.switcherette.boarribs.camera.Camera.Input
import com.switcherette.boarribs.camera.Camera.Output
import com.switcherette.boarribs.new_sighting_form.NewSightingForm
import io.reactivex.Single

interface Camera : Rib, Connectable<Input, Output> {

    interface Dependency

    sealed class Input {
        data class GrantPermissions(val permissions:List<String>) : Input()
    }

    sealed class Output {
        data class PermissionsRequired(val permissions:List<String>) : Output()
        data class PhotoTaken (val filepath: String?) : Output()
    }

    class Customisation(
        val viewFactory: CameraView.Factory = CameraViewImpl.Factory(),
    ) : RibCustomisation


}
