package com.patrol.guard.guardpatrol.model.guardTour

import com.google.gson.annotations.SerializedName

class PostId {

    @SerializedName("address")
    var address: String? = null
    @SerializedName("id")
    var id: String? = null
    @SerializedName("latitude")
    var latitude: Double? = null
    @SerializedName("longitude")
    var longitude: Double? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("_id")
    var _id: String? = null

}
