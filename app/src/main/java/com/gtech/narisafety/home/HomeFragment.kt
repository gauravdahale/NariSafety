package com.gtech.narisafety.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.gtech.narisafety.R
import com.gtech.narisafety.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    lateinit var mPrefs: SharedPreferences
    var _binding: FragmentHomeBinding? = null
    val binding get() = _binding!!
    var startjourney = ""
    var endjourney = ""
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
        mPrefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val username = mPrefs.getString("username", "")
binding.username.text= "Hello "+ returnFirstName(username)
binding.fromlayout.setOnClickListener {
    binding.fromlocation.requestFocus()

}
        binding.tolocation.setOnClickListener {
            binding.tolocation.requestFocus()
        }
    binding.submitJourney.setOnClickListener {
        startjourney = binding.fromlocation.text.toString()
        endjourney = binding.tolocation.text.toString()
    if (startjourney.isNotBlank()) binding.startlocation.text= startjourney
    if (endjourney.isNotBlank())  binding.endlocation.text= endjourney
        Toast.makeText(context, "Journey Started ", Toast.LENGTH_SHORT).show()
    }
        val list = arrayListOf<String>("15 Minutes","30 Minutes","1 Hour","2 Hour","3 Hour")
    var listadapter =ArrayAdapter<String>(requireContext(),R.layout.support_simple_spinner_dropdown_item,list)
        binding.timer.setAdapter(listadapter)
    }

    private fun returnFirstName(username: String?): String {
        var name = ""

        if (username != null && username.contains(" ")) {
            val list = username.split(" ")
            name = list[0]+","
        }
        return name
    }


override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}
}