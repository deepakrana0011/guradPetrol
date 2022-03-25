package com.patrol.guard.guardpatrol.model.login


import com.google.gson.annotations.SerializedName


class Gaurd {

    @SerializedName("createdAt")
    var createdAt: String? = null
    @SerializedName("createdOn")
    var createdOn: Long? = null
    @SerializedName("deviceId")
    var deviceId: String? = null
    @SerializedName("email")
    var email: String? = null
    @SerializedName("gaurdId")
    var gaurdId: String? = null
    @SerializedName("id")
    var id: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("status")
    var status: Long? = null
    @SerializedName("updatedAt")
    var updatedAt: String? = null
    @SerializedName("__v")
    var _V: Long? = null
    @SerializedName("_id")
    var _id: String? = null

    @SerializedName("supervisorId")
    var supervisorId:String?=null

    @SerializedName("jwtToken")
    var jwtToken:String?=null

}
