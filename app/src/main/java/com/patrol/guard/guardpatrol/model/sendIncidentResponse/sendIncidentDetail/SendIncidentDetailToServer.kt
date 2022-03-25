package com.patrol.guard.guardpatrol.model.sendIncidentResponse.sendIncidentDetail

import com.google.gson.annotations.SerializedName

class SendIncidentDetailToServer {

    @SerializedName("audio")
    var audio: String? = null
    @SerializedName("checkType")
    var checkType: Long? = null
    @SerializedName("gaurdlat")
    var gaurdlat: String? = null
    @SerializedName("gaurdlong")
    var gaurdlong: String? = null
    @SerializedName("images")
    var images: MutableList<String?>? = null
    @SerializedName("incidents")
    var incidents: MutableList<Incident>? = null
    @SerializedName("location")
    var location: String? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("nearestCheckPoint")
    var nearestCheckPoint: String? = null
    @SerializedName("tripId")
    var tripId: String? = null

    @SerializedName("supervisorId")
    var supervisorId:String?=null

    @SerializedName("gaurdId")
    var gaurdId:String?=null


}
