package com.patrol.guard.guardpatrol.model.endTrip

import com.google.gson.annotations.SerializedName

class EndDetailToServer {

    @SerializedName("latitude")
    var latitude: String? = null
    @SerializedName("longitude")
    var longitude: String? = null
    @SerializedName("updateId")
    var updateId: String? = null

}
