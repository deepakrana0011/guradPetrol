package com.patrol.guard.guardpatrol.model.getIncidentsList

import com.google.gson.annotations.SerializedName

class GetIncidentsList {
    @SerializedName("incidentList")
    var incidentList: List<IncidentList>? = null

}
