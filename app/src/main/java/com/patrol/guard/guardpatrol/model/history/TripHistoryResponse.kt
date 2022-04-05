package com.patrol.guard.guardpatrol.model.history

import com.google.gson.annotations.SerializedName
import com.patrol.guard.guardpatrol.model.guardTour.Tour
import com.patrol.guard.guardpatrol.model.guardTour.Trip


class TripHistoryResponse {
    @SerializedName("tripList")
    val tripList: List<Trip>?=null

}
