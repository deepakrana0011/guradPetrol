package com.patrol.guard.guardpatrol.model.sendIncidentResponse


import com.google.gson.annotations.SerializedName

class Incident {

    @SerializedName("actionOnIncident")
    var actionOnIncident: Long? = null
    @SerializedName("audio")
    var audio: String? = null
    @SerializedName("checkType")
    var checkType: Long? = null
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
    @SerializedName("image")
    var image: String? = null
    @SerializedName("images")
    var images: List<String>? = null
    @SerializedName("incidents")
    var incidents: List<Incident>? = null
    @SerializedName("isgeoLoc")
    var isgeoLoc: Boolean? = null
    @SerializedName("isnfc")
    var isnfc: Boolean? = null
    @SerializedName("isqr")
    var isqr: Boolean? = null
    @SerializedName("location")
    var location: String? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("nearestCheckPoint")
    var nearestCheckPoint: String? = null
    @SerializedName("nfc")
    var nfc: String? = null
    @SerializedName("nfcScan")
    var nfcScan: String? = null
    @SerializedName("qrScan")
    var qrScan: String? = null
    @SerializedName("qrcode")
    var qrcode: String? = null
    @SerializedName("status")
    var status: Long? = null
    @SerializedName("updatedAt")
    var updatedAt: String? = null
    @SerializedName("__v")
    var _V: Long? = null
    @SerializedName("_id")
    var _id: String? = null

}
