package com.patrol.guard.guardpatrol.model.startTrip

import com.google.gson.annotations.SerializedName

class StartTripDetailToServer {
    @SerializedName("gaurdId")
    var gaurdId: String? = null
    @SerializedName("latitude")
    var latitude: String? = null
    @SerializedName("longitude")
    var longitude: String? = null
    @SerializedName("tourId")
    var tourId: String? = null

}
