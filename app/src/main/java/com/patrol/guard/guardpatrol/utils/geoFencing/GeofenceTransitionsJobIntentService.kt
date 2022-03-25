package com.patrol.guard.guardpatrol.utils.geoFencing

import android.content.Context
import android.content.Intent

import android.util.Log
import androidx.core.app.JobIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.gson.Gson
import com.patrol.guard.guardpatrol.utils.Constants


import java.util.ArrayList

class GeofenceTransitionsJobIntentService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceErrorMessages.getErrorString(
                this,
                geofencingEvent.errorCode
            )
            Log.e(TAG, errorMessage)
            return
        }
        val geofenceTransition = geofencingEvent.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            val requestIdList = ArrayList<String>()
            for (i in triggeringGeofences.indices) {
                requestIdList.add(triggeringGeofences[i].requestId.replace("hundered", ""))
            }
            val gson = Gson()
            val data = gson.toJson(requestIdList)
            val geoFenceIntent = Intent(Constants.GEO_FENCE)
            geoFenceIntent.putExtra("geoFenceData", data)
            sendBroadcast(geoFenceIntent)
        } else {
            Log.e("invaild geofence", geofenceTransition.toString() + "")
        }
    }

    companion object {

        private val JOB_ID = 573

        private val TAG = "GeofenceTransitionsIS"

        private val CHANNEL_ID = "channel_01"

        fun enqueueWork(context: Context, intent: Intent) {
            JobIntentService.enqueueWork(context, GeofenceTransitionsJobIntentService::class.java, JOB_ID, intent)
        }
    }
}