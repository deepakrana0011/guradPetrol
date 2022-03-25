package com.patrol.guard.guardpatrol.model.guardTour

import com.google.gson.annotations.SerializedName

class GuardDetailToServer {

    @SerializedName("latitude")
    var latitude: Double? = null
    @SerializedName("longitude")
    var longitude: Double? = null

}
