package com.patrol.guard.guardpatrol.model.setting

import com.google.gson.annotations.SerializedName

class SettingDetailToServer {
    @SerializedName("gaurdId")
    var gaurdid: String? = null
    @SerializedName("oldPin")
    var oldPin: String? = null
    @SerializedName("newPin")
    var newPin: String? = null



}