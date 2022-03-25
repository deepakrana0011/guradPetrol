package com.patrol.guard.guardpatrol.model.guardTour

import com.google.gson.annotations.SerializedName

class CheckPoint {

    @SerializedName("audio")
    var audio: String? = null
    @SerializedName("checkType")
    var checkType: Int? = null
    @SerializedName("createdAt")
    var createdAt: String? = null
    @SerializedName("date")
    var date: Long? = null
    @SerializedName("gaurdlat")
    var gaurdlat: String? = null
    @SerializedName("gaurdlong")
    var gaurdlong: String? = null
    @SerializedName("geolat")
    var geolat: String? = null
    @SerializedName("geolong")
    var geolong: String? = null
    @SerializedName("id")
    var id: String? = null
    @SerializedName("images")
    var images: List<Any>? = null
    @SerializedName("incidents")
    var incidents: List<Any>? = null
    @SerializedName("isgeoLoc")
    var isgeoLoc: Boolean? = null
    @SerializedName("isnfc")
    var isnfc: Boolean? = null
    @SerializedName("isqr")
    var isqr: Boolean? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("nfc")
    var nfc: String? = null
    @SerializedName("nfcScan")
    var nfcScan: String? = null
    @SerializedName("nfcScanDate")
    var nfcScanDate: Long? = null
    @SerializedName("qrScan")
    var qrScan: String? = null
    @SerializedName("qrScanDate")
    var qrScanDate: Long? = null
    @SerializedName("qrcode")
    var qrcode: String? = null
    @SerializedName("status")
    var status: Int? = null
    @SerializedName("tripId")
    var tripId: String? = null
    @SerializedName("updatedAt")
    var updatedAt: String? = null
    @SerializedName("__v")
    var _V: Long? = null
    @SerializedName("_id")
    var _id: String? = null

}
