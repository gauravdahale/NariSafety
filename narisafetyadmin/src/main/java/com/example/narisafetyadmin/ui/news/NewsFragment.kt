package com.example.narisafetyadmin.ui.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.narisafetyadmin.R
import com.example.narisafetyadmin.databinding.FragmentNewsBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NewsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    var _binding: FragmentNewsBinding? = null
    val binding get() = _binding!!
    lateinit var mRecyclerview: RecyclerView
    lateinit var fab: FloatingActionButton
    lateinit var mAdapter: NewsAdapter
    lateinit var mLayoutManager: LinearLayoutManager
    var mList = ArrayList<NewsModel>()
    var shimmerFrameLayout : ShimmerFrameLayout?=null
    lateinit var mNavController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNewsBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        mRecyclerview = view.findViewById<RecyclerView>(R.id.news_recyclerview)
        binding.shimmerFrameLayout.startShimmer()
binding.addNews.setOnClickListener {
    mNavController.navigate(R.id.action_navigation_home_to_fragmentAddNews)
}
        shimmerFrameLayout =view.findViewById(R.id.shimmerFrameLayout)
        mLayoutManager = LinearLayoutManager(requireActivity())
        mAdapter = NewsAdapter(this, mList)
        mLayoutManager.stackFromEnd =true
        mLayoutManager.reverseLayout =true
        mRecyclerview.layoutManager = mLayoutManager
        mRecyclerview.adapter =mAdapter
        getnews()
    }
    private fun getnews() {
        FirebaseDatabase.getInstance().reference.child("news")
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mList.clear()
                    for (child in snapshot.children) {
                        val model = child.getValue(NewsModel::class.java)
                        mList.add(model!!)
                        Log.d("HomeFragment", "onDataChange: model ${model.heading}")
                    }
             if(mList.size>0)       shimmerFrameLayout!!.stopShimmer()
                    shimmerFrameLayout!!.visibility = View.GONE
                    mRecyclerview.visibility = View.VISIBLE
                    mAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
    override fun onResume() {
        super.onResume()
        shimmerFrameLayout?.startShimmer()
    }

    override fun onPause() {
        shimmerFrameLayout?.stopShimmer()
        super.onPause()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}