package com.patrol.guard.guardpatrol.model.login

import com.google.gson.annotations.SerializedName

class LoginDetailToServer {
    @SerializedName("deviceId")
    var deviceId: String? = null
    @SerializedName("gaurdId")
    var gaurdId: String? = null
    @SerializedName("pin")
    var pin: String? = null

}
