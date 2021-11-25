package com.vrinda.microsoft.modals

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class DashboardIconData (

    @PropertyName("iconActionName")
    var iconActionName: String? = "",

    @PropertyName("iconDrawableName")
    var iconDrawableName: Int

)

