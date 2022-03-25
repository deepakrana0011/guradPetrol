package com.patrol.guard.guardpatrol.model.startTrip

import com.google.gson.annotations.SerializedName

class PostId {

    @SerializedName("address")
    var address: String? = null
    @SerializedName("id")
    var id: String? = null
    @SerializedName("latitude")
    var latitude: Double? = null
    @SerializedName("locationId")
    var locationId: LocationId? = null
    @SerializedName("longitude")
    var longitude: Double? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("_id")
    var _id: String? = null

}
