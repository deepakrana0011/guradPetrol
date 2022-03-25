package com.patrol.guard.guardpatrol.model.sendIncidentResponse

import com.google.gson.annotations.SerializedName

class SendIncidentResponse {

    @SerializedName("incident")
    var incident: Incident? = null

}
