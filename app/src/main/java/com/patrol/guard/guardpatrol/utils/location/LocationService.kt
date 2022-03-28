package com.patrol.guard.guardpatrol.utils.location

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.observe
import com.birjuvachhani.locus.Locus
import com.birjuvachhani.locus.LocusResult
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.model.LocationData
import com.patrol.guard.guardpatrol.utils.BasicFunctions
import com.patrol.guard.guardpatrol.utils.SharedPref
import org.koin.android.ext.android.inject

class LocationService : LifecycleService() {
    val sharedPref: SharedPref by inject()
    lateinit var locationList : ArrayList<LocationData>

    companion object {
        const val NOTIFICATION_ID = 787
        const val STOP_SERVICE_BROADCAST_ACTON =
            "com.patrol.guard.guardpatrol.utils.location.ServiceStopBroadcastReceiver"
    }



    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
    private val manager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: throw Exception("No notification manager found")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.e("Service", "Temp Service started")

        Handler(Looper.getMainLooper()).postDelayed({
            start()
        }, 500)
        return START_STICKY
    }

    private fun start() {
        startForeground(NOTIFICATION_ID, getNotification())
        Locus.configure {
            enableBackgroundUpdates = true
        }
        Locus.startLocationUpdates(this).observe(this) { result ->

            result?.let {

                it.location.let {
                    BasicFunctions.addLocationData(result.location?.latitude?:0.0,result.location?.longitude?:0.0, sharedPref )

                }


              //  Log.e("Location11", "Latitude: ${result.location?.latitude}\tLongitude: ${location.longitude}")
            }
          //  manager.notify(NOTIFICATION_ID, getNotification(result))
        }
    }

    private fun getNotification(result: LocusResult? = null): Notification {



        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: throw Exception("No notification manager found")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    "location",
                    "Location Updates",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
        return with(NotificationCompat.Builder(this, "location")) {
            setContentTitle("Location Service")
            result?.apply {
                location?.let {
                    setContentText("${it.latitude}, ${it.longitude}")
                } ?: setContentText("Error: ${error?.message}")
            } ?: setContentText("Trying to get location updates")
            setSmallIcon(R.drawable.ic_camera)
            setAutoCancel(false)
            setOnlyAlertOnce(true)
            val flags =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                else PendingIntent.FLAG_UPDATE_CURRENT
            /*addAction(
                0,
                "Stop Updates",
                PendingIntent.getBroadcast(
                    this@LocationService,
                    0,
                    Intent(this@LocationService, ServiceStopBroadcastReceiver::class.java).apply {
                        action = STOP_SERVICE_BROADCAST_ACTON
                    },
                    flags
                )
            )*/
            build()
        }
    }
}