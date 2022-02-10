package com.gtech.narisafety

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


class MainActivity : AppCompatActivity() {
    var PERMISSION_ID = 44
    var mFusedLocationClient: FusedLocationProviderClient? = null
    lateinit var news: TextView
    lateinit var newsbar: View
    lateinit var journey: TextView
    lateinit var journeybar: View
    private val TAG = "MainActivity"
    lateinit var navController: NavController
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private val mCurrentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        news = findViewById(R.id.news)
//        newsbar = findViewById(R.id.newsbar)
//        journey = findViewById(R.id.journey)
//        journeybar = findViewById(R.id.journeybar)

        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.SCHEDULE_EXACT_ALARM,
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    isLocationEnabled()

                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) { /* ... */
                }
            }).check()
        navController = findNavController(R.id.nav_host_fragment)
        Log.d(TAG, "Current User: $mCurrentUser")
        Log.d(TAG, "UID: ${mCurrentUser?.uid}")
        if (mCurrentUser?.uid == null) {
            navController.navigate(R.id.action_homeFragment_to_loginFragment)
            Log.d(TAG, "onCreate: $mCurrentUser ")
            Log.d(TAG, "onCreate: Going to Login")

        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
setFCM()
    }

    private fun setFCM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName, NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }

    }

    private fun requestPermissions() {
        // below line is use to request
        // permission in the current activity.
        Dexter.withContext(this) // below line is use to request the number of
            // permissions which are required in our app.
            .withPermissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION

            ) // after adding permissions we are
            // calling an with listener method.
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    // this method is called when all permissions are granted
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        // do you work now
                        Toast.makeText(
                            this@MainActivity,
                            "All the permissions are granted..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    // check for permanent denial of any permission
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        // permission is denied permanently,
                        // we will show user a dialog message.
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest?>?,
                    permissionToken: PermissionToken
                ) {
                    // this method is called when user grants some
                    // permission and denies some of them.
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener { // we are displaying a toast message for error message.
                Toast.makeText(applicationContext, "Error occurred! ", Toast.LENGTH_SHORT).show()
            } // below line is use to run the permissions
            // on same thread and to check the permissions
            .onSameThread().check()
    }

    private fun showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        val builder = AlertDialog.Builder(this)

        // below line is the title
        // for our alert dialog.
        builder.setTitle("Need Permissions")

        // below line is our message for our dialog
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS",
            DialogInterface.OnClickListener { dialog, which -> // this method is called on click on positive
                // button and on clicking shit button we
                // are redirecting our user from our app to the
                // settings page of our app.
                dialog.cancel()
                // below is the intent from which we
                // are redirecting our user.
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivityForResult(intent, 101)
            })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> // this method is called when
                // user click on negative button.
                dialog.cancel()
            })
        // below line is used
        // to display our dialog
        builder.show()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient?.getLastLocation()
                    ?.addOnCompleteListener(OnCompleteListener { task ->
                        val location: Location? = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
//                            latitudeTextView.setText(location.getLatitude().toString() + "")
//                            longitTextView.setText(location.getLongitude().toString() + "")
                        }
                    })
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        val mLocationRequest = LocationRequest()
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setInterval(5)
        mLocationRequest.setFastestInterval(0)
        mLocationRequest.setNumUpdates(1)

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient?.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
//            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude().toString() + "")
//            longitTextView.setText("Longitude: " + mLastLocation.getLongitude().toString() + "")
        }
    }

    // method to check for permissions
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,

            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    // method to request for permissions
//    private fun requestPermissions() {
//        requestPermissions(
//            this, arrayOf(
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ), PERMISSION_ID
//        )
//    }

    // method to check
    // if location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // If everything is alright then
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        checkPermissions()
    }

    fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken) {
        token.continuePermissionRequest()
    }

    companion object {

    }

}