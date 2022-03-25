package com.patrol.guard.guardpatrol.model.sendIncidentResponse.sendIncidentDetail

import com.google.gson.annotations.SerializedName

class Incident {

    @SerializedName("image")
    var image: String? = null
    @SerializedName("name")
    var name: String? = null

}
