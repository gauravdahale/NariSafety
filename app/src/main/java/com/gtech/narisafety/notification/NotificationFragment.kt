package com.gtech.narisafety.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gtech.narisafety.R

/**
 * A fragment representing a list of Items.
 */
class NotificationFragment : Fragment() {

    lateinit var mAdapter: MyNotificationsAdapter
    private val mlist= ArrayList<GetNotification>()
    private val mCurrentPhone= FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()
    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification_list, container, false)
        mAdapter = MyNotificationsAdapter(mlist)
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = mAdapter
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    getNotifications()
    }

    private fun getNotifications() {
        FirebaseDatabase.getInstance().reference.child("usersbynumbersnotifications").child(mCurrentPhone).child("notifications").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mlist.clear()
snapshot.children.forEach {
    val md = it.getValue(GetNotification::class.java)
mlist.add(md!!)
mAdapter.notifyDataSetChanged()
}
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            NotificationFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
class   GetNotification(){
    var msg:String?=null
    var sentby:String?=null
    var senybyuid:String?=null
    var timestamp:Long?=null
}