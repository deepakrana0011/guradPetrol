package com.patrol.guard.guardpatrol.model.startTrip

import com.google.gson.annotations.SerializedName

class Tour {

    @SerializedName("checkPoints")
    var checkPoints: List<CheckPoint>? = null
    @SerializedName("client")
    var client: String? = null
    @SerializedName("createdAt")
    var createdAt: String? = null
    @SerializedName("endTime")
    var endTime: String? = null
    @SerializedName("id")
    var id: String? = null
    @SerializedName("location")
    var location: String? = null
    @SerializedName("postId")
    var postId: PostId? = null
    @SerializedName("startTime")
    var startTime: String? = null
    @SerializedName("status")
    var status: Int? = null
    @SerializedName("updatedAt")
    var updatedAt: String? = null
    @SerializedName("__v")
    var _V: Long? = null
    @SerializedName("_id")
    var _id: String? = null

}
