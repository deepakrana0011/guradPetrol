package com.patrol.guard.guardpatrol.model.getIncidentsList

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName
import java.io.File

class IncidentList {

    @SerializedName("id")
    var id: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("_id")
    var _id: String? = null


    var isSelected: Boolean = false
    var bitMap: Bitmap? = null

    var imageUrl:String?=null

}
