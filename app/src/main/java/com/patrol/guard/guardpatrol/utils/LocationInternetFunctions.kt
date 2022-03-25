package com.patrol.guard.guardpatrol.utils

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.patrol.guard.guardpatrol.utils.Constants.REQUEST_CHECK_LOCATION_STATUS


class LocationInternetFunctions {
    companion object {
        lateinit var mInstance: LocationInternetFunctions
    }

    init {
        mInstance = this
    }

    fun deafultGoogleLocationDialog(activity: Activity) {
        var locationRequest = LocationRequest().apply {
            interval = 1000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        val result = LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build())
        result.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)

            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        try {
                            val resolvable = exception as ResolvableApiException
                            ActivityCompat.startIntentSenderForResult(activity,resolvable.getResolution().getIntentSender(), REQUEST_CHECK_LOCATION_STATUS, null, 0, 0, 0, null);

                        } catch (e: IntentSender.SendIntentException) {

                        } catch (e: ClassCastException) {

                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }

    fun isGpsOn(activity: Activity): Boolean {
        val manager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

}
