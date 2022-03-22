package com.gtech.narisafety.home

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.gtech.narisafety.R
import com.gtech.narisafety.builder.AlarmBuilder
import com.gtech.narisafety.databinding.FragmentHomeBinding
import com.gtech.narisafety.enums.AlarmType
import com.gtech.narisafety.interfaces.IAlarmListener
import com.gtech.narisafety.services.AlarmService
import com.gtech.narisafety.services.GPSTracker
import com.gtech.narisafety.services.NotificationUtils
import java.util.*
import java.util.concurrent.TimeUnit

//Alarm Setup Here
class HomeFragment : Fragment(), IAlarmListener {
    lateinit var mPrefs: SharedPreferences
    lateinit var mNavController: NavController

    var _binding: FragmentHomeBinding? = null
    val binding get() = _binding!!
    var startjourney = ""
    var endjourney = ""
    var setSeconds = 10
    var setMillis = 10000L
    val list = ArrayList<TimeModel>()
    var timerset = false
    var builder: AlarmBuilder? = null;
    val liststring = ArrayList<String>()
    val currentuser = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val mReference = FirebaseDatabase.getInstance().reference.child("journeys").child(currentuser!!)
    val key =
        FirebaseDatabase.getInstance().reference.child("journeys").child(currentuser).push().key

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        mPrefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val username = mPrefs.getString("username", "")
        binding.newsbar.visibility = View.GONE
        binding.journeybar.visibility = View.GONE
        binding.news.setOnClickListener {
            mNavController.navigate(R.id.newsFragment)
            binding.newsbar.visibility = View.GONE
            binding.journeybar.visibility = View.GONE
        }
        binding.journey.setOnClickListener {
            mNavController.navigate(R.id.homeFragment)
            binding.newsbar.visibility = View.GONE
            binding.journeybar.visibility = View.GONE
        }


//Alarm Builder

        //creating alarm builder
        if (mPrefs.getString("sos", "") != "") {
            binding.sosnumber.setText(mPrefs.getString("sos", ""))

        }

        binding.username.text = "Hello " + returnFirstName(username)
        binding.fromlayout.setOnClickListener {
            binding.fromlocation.requestFocus()

        }
        binding.tolocation.setOnClickListener {
            binding.tolocation.requestFocus()
        }
        binding.addlocation.setOnClickListener {
            mNavController.navigate(R.id.action_homeFragment_to_addLocationDialogFragment)
        }
        binding.submitJourney.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("usersbynumber")
                .child(binding.sosnumber.text.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            builder = AlarmBuilder().with(requireContext())
//        .setTimeInMilliSeconds(TimeUnit.SECONDS.toMillis(setSeconds.toLong()))
                                .setTimeInMilliSeconds(TimeUnit.SECONDS.toMillis(10))
                                .setId("UPDATE_INFO_SYSTEM_SERVICE")
                                .setAlarmType(AlarmType.ONE_TIME)

                            if (binding.sosnumber.text.toString()
                                    .isNotBlank() && binding.tolocation.text.toString()
                                    .isNotBlank()
                                && binding.fromlocation.text.toString().isNotBlank()
                                && timerset
                            ) {
                                startjourney = binding.fromlocation.text.toString()
                                endjourney = binding.tolocation.text.toString()
                                if (startjourney.isNotBlank()) binding.startlocation.text =
                                    startjourney
                                if (endjourney.isNotBlank()) binding.endlocation.text = endjourney
                                Log.d("NariSafety", "Timeinmillis:$setSeconds ")
                                Log.d("NariSafety", "Timeinmillis:$setMillis ")
                                //  Toast.makeText(context, "Journey Started in $setmillis", Toast.LENGTH_SHORT).show()

                                mPrefs.edit().putString("sos", binding.sosnumber.text.toString())
                                    .apply()
                                val msg =
                                    "Hi I am at" + "latitude" + "/n longitude" + "/n was going from ${binding.fromlocation.text.toString()} to ${binding.tolocation.text.toString()}. "

                                Log.d("NariSafety", "msg: $msg")
                                Log.d("NariSafety", "Setting Alarm Now")
                                val journeyModel = JourneyModel().apply {
                                    start = binding.startlocation.text.toString()
                                    end = binding.endlocation.text.toString()
                                    sosnumber = binding.sosnumber.text.toString()
                                    timing = binding.timer.text.toString()
                                    middle = locatlionlist
                                }

                                mReference.setValue(journeyModel)
                                builder
//                                .setAlarm()
//Creating a bundle to send with intent when alarm triggers



                                setAlarm(setSeconds,journeyModel)

                            } else if (binding.fromlocation.text.toString().isEmpty()) {
                                binding.fromlocation.requestFocus()
                                binding.fromlocation.error = "Enter Location"
                            } else if (binding.tolocation.text.toString().isEmpty()) {
                                binding.tolocation.requestFocus()
                                binding.tolocation.error = "Enter Location"
                            } else if (binding.sosnumber.text.toString().isEmpty()) {
                                binding.sosnumber.requestFocus()
                                binding.sosnumber.error = "Enter SOS Number "
                            } else if (!timerset) {
                                binding.timer.requestFocus()
                                binding.timer.error = "Enter Journey Duration"
                            }
                        } else Toast.makeText(
                            context,
                            "Number is not a valid user",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

        }
        addtimerItems()
        list.forEach { liststring.add(it.name!!) }
        var listadapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            liststring
        )
        binding.timer.setAdapter(listadapter)
        binding.timer.setOnItemClickListener { parent, view, position, id ->
            setSeconds = list[position].secs!!
            setMillis = list[position].millis!!
            Log.d("NariSafety", "AdapterTimeinmillis:$setSeconds ")
            timerset = true
        }
        val mReyclerView = binding.localtionRecyclerview

        mReyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = locationadapter
        }

    }

    private fun addtimerItems() {
        val md1 = TimeModel().apply {
            name = "15 Minutes"
            secs = 60 * 1
            millis = 60000 * 1
        }
        val md2 = TimeModel().apply {
            name = "30 Minutes"
            secs = 60 * 30
            millis = 60000 * 30
        }
        val md3 = TimeModel().apply {
            name = "45 Minutes"
            secs = 60 * 45
            millis = 60000 * 45
        }
        val md4 = TimeModel().apply {
            name = "60 Minutes"
            secs = 60 * 60
            millis = 60000 * 60
        }
        val md5 = TimeModel().apply {
            name = "90 Minutes"
            secs = 60 * 90
            millis = 60000 * 90
        }
        val md6 = TimeModel().apply {
            name = "2 hours"
            secs = 60 * 120
            millis = 60000 * 120
        }
        list.add(md1)
        list.add(md2)
        list.add(md3)
        list.add(md4)
        list.add(md5)
        list.add(md6)
    }

    private fun returnFirstName(username: String?): String {
        var name = ""

        if (username != null && username.contains(" ")) {
            val list = username.split(" ")
            name = list[0] + ","
        }
        return name
    }

    private fun setAlarm(times: Int, jmodel: JourneyModel) {

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.SECOND, times)
        Toast.makeText(context, " $times", Toast.LENGTH_SHORT).show()
        //getting the alarm manager
        val am = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager?

        //creating a new intent specifying the broadcast receiver
        val i = Intent(requireContext(), AlarmService::class.java)
        i.putExtra("jmodel", jmodel)
        //creating a pending intent using the intent
        val pi = PendingIntent.getBroadcast(requireContext(), 0, i, 0)

        //setting the repeating alarm that will be fired every day
        am!!.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pi)

    //        Toast.makeText(context, "Alarm is set", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        builder?.addListener(this)
    }

    override fun onPause() {
        super.onPause()
        builder?.removeListener(this)

    }

    override fun perform(context: Context, intent: Intent) {
        Log.d("NariSafety", "perform Alarm just fired")
        val notificationUtils = NotificationUtils(context)
        val notification = notificationUtils.getNotificationBuilder()
            .setContentText("Perform task ")
            .build()
        notificationUtils.getManager().notify(150, notification)
        //you can check the log that it is fired
        //Here we are actually not doing anything
        //but you can do any task here that you want to be done at a specific time everyday
        val gps = GPSTracker(context)

        // Check if GPS enabled

        // Check if GPS enabled
        if (gps.canGetLocation()) {
            val latitude: Double = gps.latitude
            val longitude: Double = gps.longitude
            // \n is for new line
            Toast.makeText(
                context,
                "Your Location is - \nLat: $latitude\nLong: $longitude", Toast.LENGTH_LONG
            ).show()
            try {
                val smsManager: SmsManager = SmsManager.getDefault()
                val msg = "Hi I am at" + latitude + "/n $longitude" + "/n was going from ${binding.fromlocation.text.toString()} to ${binding.tolocation.text.toString()}. +" +
                        "http://maps.google.com/?q=$latitude,$longitude\n"
                Log.d("NariSafety", "msg: $msg")
                val number = mPrefs.getString("sos", "sd")
                val firemap = HashMap<String, Any>()
                firemap["sentby"] = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()
                firemap["sentbyuid"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
                firemap["msg"] = msg
                firemap["timestamp"] = ServerValue.TIMESTAMP
                firemap["sos"] = number.toString()
                FirebaseDatabase.getInstance().reference.child("usersbynumber")
                    .child("notifications").push().setValue(firemap)

                Toast.makeText(
                    requireContext().applicationContext, "Message Sent",
                    Toast.LENGTH_LONG
                ).show()

            } catch (ex: Exception) {
                Log.e("NariSafety", "perform sms operation error:${ex.localizedMessage} ")
                Toast.makeText(
                    requireContext().applicationContext, ex.message.toString(),
                    Toast.LENGTH_LONG
                ).show()
                ex.printStackTrace()
            }
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert()
        }
    }

    companion object {
        private const val TAG = "HomeFragment"
        val locatlionlist = ArrayList<String>()
        val locationadapter = LocationAdapter(locatlionlist)
    }
}

class TimeModel() {
    var name: String? = null
    var secs: Int? = null
    var millis: Long? = null
}