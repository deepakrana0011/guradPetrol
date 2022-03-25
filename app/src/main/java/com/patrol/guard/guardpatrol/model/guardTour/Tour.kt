package com.patrol.guard.guardpatrol.model.guardTour

import com.google.gson.annotations.SerializedName

class Tour {

    @SerializedName("checkPoints")
    var checkPoints: MutableList<CheckPoint>? = null
    @SerializedName("client")
    var client: String? = null
    @SerializedName("createdAt")
    var createdAt: String? = null

    @SerializedName("checkPointPosition")
    var checkPointPosition:Int?=null


    @SerializedName("id")
    var id: String? = null
    @SerializedName("location")
    var location: String? = null
    @SerializedName("postId")
    var postId: PostId? = null
    @SerializedName("startTime")
    var startTime: String? = null

    @SerializedName("endTime")
    var endTime:String?=null

    @SerializedName("tripStartTime")
    var tripStartTime:Long?=null

    @SerializedName("status")
    var status: Int? = null
    @SerializedName("updatedAt")
    var updatedAt: String? = null
    @SerializedName("__v")
    var _V: Long? = null
    @SerializedName("_id")
    var _id: String? = null

    @SerializedName("date")
    var date:Long?=null


}
