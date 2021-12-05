package com.gtech.narisafety

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private  val TAG = "MainActivity"
    lateinit var navController : NavController
    private  var mFirebaseAnalytics: FirebaseAnalytics? = null
    private val mCurrentUser   =FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = findNavController(R.id.nav_host_fragment)
        Log.d(TAG, "Current User: $mCurrentUser")
        Log.d(TAG, "UID: ${mCurrentUser?.uid}")
        if(mCurrentUser?.uid==null){
        navController.navigate(R.id.action_homeFragment_to_loginFragment)
        Log.d(TAG, "onCreate: $mCurrentUser ")
        Log.d(TAG, "onCreate: Going to Login")

    }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }
}