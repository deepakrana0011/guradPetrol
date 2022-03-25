package com.patrol.guard.guardpatrol.model

import com.google.gson.annotations.SerializedName

class SingleTripDetail {

    @SerializedName("currentCheckPoint")
    var currentCheckPoint: String? = null
    @SerializedName("tripId")
    var tripId: String? = null

}
