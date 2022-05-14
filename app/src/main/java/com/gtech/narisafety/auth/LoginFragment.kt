package com.gtech.narisafety.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.gtech.narisafety.R
import com.gtech.narisafety.databinding.FragmentLoginBinding
import java.lang.Exception
import java.text.DateFormat
import java.util.*
import androidx.navigation.NavController
import com.google.firebase.FirebaseException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.gtech.narisafety.MainActivity
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class LoginFragment : Fragment() {
    private  val TAG = "LoginFragment"
    var _binding: FragmentLoginBinding? = null
    lateinit var mPrefs : SharedPreferences
    val binding get() = _binding!!
    var currentstep = 1
    private var mAuth: FirebaseAuth? = null
    private var mResendToken: ForceResendingToken? = null
    private var mVerificationId: String? = null
    private var mDatabaseReference: DatabaseReference? = null
    private lateinit var mobile: String
    private lateinit var name: String
 lateinit var mNavController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
     mNavController = Navigation.findNavController(view)
  mPrefs = requireContext().getSharedPreferences("prefs",Context.MODE_PRIVATE)
        mAuth = FirebaseAuth.getInstance()
        mDatabaseReference = FirebaseDatabase.getInstance().reference
binding.submitStep1.setOnClickListener {
    if (!binding.authMobile.text.isNullOrEmpty() && binding.authMobile.text!!.length >= 10 && binding.authName.text.toString()
            .isNotBlank()
    ) {
        mobile = binding.authMobile.text.toString()
        name = binding.authName.text.toString()
        Toast.makeText(context, "Mobile : $mobile", Toast.LENGTH_SHORT).show()
        sendVerificationCode(mobile)
        binding.step1.visibility = View.INVISIBLE
        binding.step2.visibility = View.VISIBLE


    } else Toast.makeText(context, "Please Enter a Valid Number", Toast.LENGTH_SHORT).show()
}
    binding.submitStep2.setOnClickListener {

            val code = binding.code.text.toString().trim { it <= ' ' }
            if (code.isEmpty() || code.length < 6) {
                binding.code.error = "Enter valid code"
                binding.code.requestFocus()
                return@setOnClickListener
            }
            //verifying the code entered manually
            try {
                verifyVerificationCode(code)
            } catch (e: Exception) {
                Log.d("submitotp", "${e.message}")
            }

        }





    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //the callback to detect the verification status
    private val mCallbacks: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) { //Getting the code sent by SMS
                val smscode = phoneAuthCredential.smsCode
                //sometime the code is not detected automatically
//in this case the code will be null
//so user has to manually enter the code
                if (smscode != null) {
                    binding.code.setText(smscode)
                    //verifying the code
                    verifyVerificationCode(smscode)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                //storing the verification id that is sent to the user
                mVerificationId = s
                mResendToken = forceResendingToken

            }
        }

    private fun verifyVerificationCode(code: String) { //creating the credential
//signing the user
        try {
            val credential = getCredential((mVerificationId)!!, code)
            signInWithPhoneAuthCredential(credential)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "verifyVerificationCode" + e.localizedMessage)
            // startActivity( new Intent(VerifyPhoneActivity.this,DrawerActivity.class));
//            finish()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) { //verification successful we will start the profile activity
                    //User Data Feeding to database.
//                    val tags = JSONObject()
//                    try {
//                        tags.put("Id", token)
//                        tags.put("Name", name)
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                    OneSignal.sendTags(tags)
//                    val datetime = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
                    registerUser(binding.authName,binding.authMobile)
                } else { //verification unsuccessful.. display an error message
                    var message = "Something is wrong, we will fix it soon..."
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        message = "Invalid code entered..."
                    }
                    val snackbar =
                        Snackbar.make(binding.root, (message), Snackbar.LENGTH_LONG)
                    snackbar.setAction("Dismiss") { }
                    snackbar.show()
                }
            }
    }

    private fun registerUser(authName: EditText, authMobile: EditText) {
        val map =HashMap<String,Any>()
        map["name"] = authName.text.toString()
        map["mobile"] = authMobile.text.toString()
        map["timestamp"] = ServerValue.TIMESTAMP
    val editor = mPrefs.edit()
        editor.putString("username",authName.text.toString())
        editor.apply()
        FirebaseDatabase.getInstance().reference.child("users").child(mAuth?.currentUser?.uid.toString()).updateChildren(map).addOnCompleteListener {
            FirebaseDatabase.getInstance().reference.child("usersbynumber").child(authMobile.text.toString()).updateChildren(map)
            mNavController.navigate(R.id.action_loginFragment_to_addJourneyFragment)
        }
    }
    //the method is sending verification code
//the country id is concatenated
//you can take the country id as user input as well
    private fun sendVerificationCode(mobile: String?) {
        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber("+91$mobile")
            .setTimeout(30L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(mCallbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendVerificationCode(
        mobile: String?,
        mResendToken: ForceResendingToken?
    ) {

        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber("+91$mobile")
            .setTimeout(30L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(mCallbacks)
            .setForceResendingToken(mResendToken!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
//        getInstance().verifyPhoneNumber(
//            "+91$mobile",  // Phone number to verify
//            30,  // Timeout duration
//            TimeUnit.SECONDS,  // Unit of timeout
//            this,  // Activity (for callback binding)
//            mCallbacks,  // OnVerificationStateChangedCallbacks
//            mResendToken
//        ) // ForceResendingToken from callbacks
        Toast.makeText(requireContext(), "OTP has been sent again", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Fragment Destroyed")
    }
}