package com.example.narisafetyadmin.ui.dashboard

import GetUserModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.example.narisafetyadmin.R
import com.facebook.shimmer.ShimmerFrameLayout

class UsersFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mAdapter: UserAdapter
    lateinit var mRecyclerView: RecyclerView
    lateinit var mNavController:NavController
    lateinit var mReference: Query
    lateinit var listener: ValueEventListener
    lateinit var searchView:SearchView
    lateinit var shimmerFrameLayout:ShimmerFrameLayout
    val mlist = ArrayList<GetUserModel>()
    //    lateinit var url: String
    var querytype:String?=null
    private  val TAG = "UserFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        mRecyclerView = view.findViewById<RecyclerView>(R.id.member_recyclerview)
searchView =view.findViewById<SearchView>(R.id.searchView)
        shimmerFrameLayout = view.findViewById<ShimmerFrameLayout>(R.id.shimmerFrameLayout)
//        url = arguments?.getString("parcel") as String
        mAdapter = UserAdapter(this, mlist)
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
//        Toast.makeText(requireContext(), "Karyakarini : $url", Toast.LENGTH_SHORT).show()
        Log.d("QUERY","QUery : $querytype")

        mReference =     FirebaseDatabase.getInstance().reference.child("users")
        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mlist.clear()
                snapshot.children.forEach {
                    val model = it.getValue(GetUserModel::class.java)

                    model?.key = it.key
                    mlist.add(model!!)

                }
                mlist.reverse()
                Log.d("members list","${mlist.size}")
      view.findViewById<ShimmerFrameLayout>(R.id.shimmerFrameLayout).stopShimmer()
                view.findViewById<ShimmerFrameLayout>(R.id.shimmerFrameLayout).visibility = View.GONE
                mRecyclerView.visibility = View.VISIBLE
                mAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
        if(querytype==null) {
            getMembersData()
//         Toast.makeText(requireContext(), "getMembers Called", Toast.LENGTH_SHORT).show()
        }
        else if (querytype != null) {
            getMembersData (querytype!!, "url")
//         Toast.makeText(requireContext(), "getMembers with query called", Toast.LENGTH_SHORT).show()

        }
        view.findViewById<SearchView>(R.id.searchView).setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val inputMethod =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethod.hideSoftInputFromWindow(searchView?.windowToken, 0)
//                listadapter =
//                    mRecyclerview.adapter as ItemListAdapter?
                //  listadapter?.filter?.filter(p0)
//                listadapter?.notifyDataSetChanged()


                Toast.makeText(
                    activity!!.applicationContext, "Search Completed...",
                    Toast.LENGTH_SHORT
                ).show()
                return true

            }

            override fun onQueryTextChange(p0: String?): Boolean {
                //Start filtering the list as user start entering the characters
                mAdapter = mRecyclerView.adapter as UserAdapter
                Log.d("onQueryTextChange", "msg $p0")
                mAdapter?.getFilter()?.filter(p0)
                //listadapter?.notifyDataSetChanged()
//                Toast.makeText(
//                    requireActivity(),
//                    "On Text Change ${mAdapter.mList.size}",
//                    Toast.LENGTH_SHORT
//                ).show()
                Log.d(TAG, "onQueryTextChange() returned: ${mlist.size}")
                return true
            }
        })
    }

    private fun getMembersData() {
        mReference.addValueEventListener(listener)
    }
    private fun getMembersData(query:String,url:String) {
        Log.d("getMembersData","url $url query $query")
        Log.d("getMembersData"," $url")
        mReference = FirebaseDatabase.getInstance().reference.child("users").orderByChild("timestamp")

        mReference.addValueEventListener(listener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mReference.removeEventListener(listener)
    }


    override fun onResume() {
        super.onResume()
        shimmerFrameLayout.startShimmer()
    }

    override fun onPause() {
        shimmerFrameLayout.stopShimmer()
        super.onPause()

    }

}