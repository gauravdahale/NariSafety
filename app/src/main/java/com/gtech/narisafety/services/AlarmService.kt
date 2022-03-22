package com.gtech.narisafety.services

import android.content.Intent

import android.content.BroadcastReceiver
import android.content.Context
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.gtech.narisafety.home.JourneyModel
import java.util.HashMap


class AlarmService : BroadcastReceiver() {
    private  val TAG = "AlarmService"
    //the method will be fired when the alarm is triggerred
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationUtils = NotificationUtils(context!!)
        val notification = notificationUtils.getNotificationBuilder().
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

            // Check if GPS enabled

            // Check if GPS enabled
           val b =intent?.getSerializableExtra("jmodel") as JourneyModel?
            Log.d(TAG, "onReceive: ${b?.start}")
            Log.d(TAG, "onReceive: ${b?.sosnumber}")
            Log.d(TAG, "onReceive: ${b?.end}")
            Log.d(TAG, "onReceive: ${b?.middle}")
            Log.d(TAG, "onReceive: ${b?.timing}")
            try {
                var msg = ""
                val smsManager: SmsManager = SmsManager.getDefault()
               if(b?.middle?.size!=0 )     {
                   var stops = ""
                   b?.middle?.forEach {
                       stops  + "$it, "
                   }
                   msg =
                            "Hi I am at" + latitude + "/n $longitude" + "/n was going from ${b?.start} to ${b?.end} with stops: $stops . +" +
                                    "http://maps.google.com/?q=$latitude,$longitude\n"
                    }
                else {
                    msg =
                       "Hi I am at" + latitude + "/n $longitude" + "/n was going from ${b.start} to ${b.end}. +" +
                               "http://maps.google.com/?q=$latitude,$longitude\n"
                }
                    Log.d("NariSafety", "msg: $msg")
                    val number =b
                    val firemap = HashMap<String, Any>()
                    firemap["sentby"] = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()
                    firemap["sentbyuid"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
                    firemap["msg"] = msg
                    firemap["timestamp"] = ServerValue.TIMESTAMP
                    firemap["sos"] = number.toString()
                    FirebaseDatabase.getInstance().reference.child("usersbynumber").child(number.toString())
                        .child("notifications").push().setValue(firemap)



                } catch (ex: Exception) {
                    Log.e("NariSafety", "perform sms operation error:${ex.localizedMessage} ")
                    ex.printStackTrace()
                    Log.e(TAG, "onReceive: $ex.message", )
                }


        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert()
        }
    }

}