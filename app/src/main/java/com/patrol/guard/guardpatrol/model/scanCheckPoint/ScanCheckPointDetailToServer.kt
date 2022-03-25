package com.patrol.guard.guardpatrol.model.scanCheckPoint

import com.google.gson.annotations.SerializedName

class ScanCheckPointDetailToServer {

    @SerializedName("latitude")
    var latitude: String? = null
    @SerializedName("longitude")
    var longitude: String? = null
    @SerializedName("nfcScan")
    var nfcScan: String? = null
    @SerializedName("qrScan")
    var qrScan: String? = null
  /*  @SerializedName("status")
    var status: Long? = null*/
    @SerializedName("updateId")
    var updateId: String? = null


    @SerializedName("nextUpdateId")
    var nextUpdateId:String?=null

}
