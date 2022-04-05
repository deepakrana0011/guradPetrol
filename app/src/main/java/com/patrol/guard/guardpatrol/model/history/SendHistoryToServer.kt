package com.patrol.guard.guardpatrol.model.history

import com.google.gson.annotations.SerializedName

class SendHistoryToServer {

    @SerializedName("date")
    var date: String? = null

    @SerializedName("gaurdId")
    var gaurdId: String? = null

    @SerializedName("skip")
    var skip: String? = null
}