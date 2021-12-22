package com.gtech.narisafety.services

import android.content.Intent

import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import android.widget.Toast


class AlarmService : BroadcastReceiver() {

    //the method will be fired when the alarm is triggerred
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationUtils = NotificationUtils(context!!)
        val notification = notificationUtils.getNotificationBuilder()
           .
        build()
        notificationUtils.getManager().notify(150, notification)
        //you can check the log that it is fired
        //Here we are actually not doing anything
        //but you can do any task here that you want to be done at a specific time everyday
        Log.d("NariSafety", "Alarm just fired")
     val   gps = GPSTracker(context)

        // Check if GPS enabled

        // Check if GPS enabled
        if (gps.canGetLocation()) {
            val latitude: Double = gps.getLatitude()
            val longitude: Double = gps.getLongitude()

            // \n is for new line
            Toast.makeText(
                context,
                "Your Location is - \nLat: $latitude\nLong: $longitude", Toast.LENGTH_LONG
            ).show()
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert()
        }
    }

}