package com.gtech.narisafety

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gtech.narisafety.builder.AlarmBuilder
import com.gtech.narisafety.databinding.FragmentAddJourneyBinding
import com.gtech.narisafety.home.GetJourneyModel
import com.gtech.narisafety.home.LocationAdapter

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AddJourneyFragment : Fragment() {
    private lateinit var mNavController: NavController
    var _binding: FragmentAddJourneyBinding? = null
    val binding get() = _binding!!
    val currentuser = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val middleList = ArrayList<String>()
    lateinit var mRecyclerView: RecyclerView
    var builder: AlarmBuilder? = null;
    val mReference = FirebaseDatabase.getInstance().reference.child("journeys").child(currentuser)
    lateinit var mAdapter: LocationAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddJourneyBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        mAdapter = LocationAdapter(middleList)
        mRecyclerView = binding.localtionRecyclerview
        binding.newsbar.visibility = View.GONE
        binding.journeybar.visibility = View.VISIBLE
        binding.news.setOnClickListener {
            mNavController.navigate(R.id.newsFragment)
            binding.newsbar.visibility = View.GONE
            binding.journeybar.visibility = View.VISIBLE
        }
        binding.journey.setOnClickListener {
            mNavController.navigate(R.id.addJourneyFragment)
            binding.newsbar.visibility = View.VISIBLE
            binding.journeybar.visibility = View.GONE
        }
        binding.goToJourney.setOnClickListener {
            mNavController.navigate(R.id.homeFragment)
            binding.newsbar.visibility = View.VISIBLE
            binding.journeybar.visibility = View.GONE
        }
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
        setJourneyDashboard(currentuser)
        binding.cancelJourney.setOnClickListener {
            builder = AlarmBuilder().with(requireContext())
            builder?.cancelAlarm()
            mReference.removeValue().addOnSuccessListener {
                binding.startlocation.text = ""
                binding.endlocation.text = ""
                middleList.clear()
                mAdapter.notifyDataSetChanged()

            }
            binding.localtionRecyclerview.visibility = View.VISIBLE

        }
    }

    private fun setJourneyDashboard(currentuser: String) {
        if (!currentuser.isNullOrEmpty()) {
            mReference.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.localtionRecyclerview.visibility = View.VISIBLE

                    middleList.clear()
                    val md = snapshot.getValue(GetJourneyModel::class.java)
                    md?.start?.let { binding.startlocation.text = it }
                    md?.end?.let { binding.endlocation.text = it }
                    md?.middle?.let {
                        middleList.addAll(it)
                        mAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }


}