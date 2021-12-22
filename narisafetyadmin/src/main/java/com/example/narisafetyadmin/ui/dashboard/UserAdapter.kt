package com.example.narisafetyadmin.ui.dashboard

import GetUserModel
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.codestrela.riteshenterprises.listeners.ItemClickListener
import com.example.narisafetyadmin.R
import com.example.narisafetyadmin.databinding.ItemUsersBinding

import java.util.*
import kotlin.collections.ArrayList


class UserAdapter(val mContext: Fragment, private val mList: ArrayList<GetUserModel>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>(), Filterable {
    internal var filter: CustomFilter? = null
    internal var itemList: ArrayList<GetUserModel> = mList
    internal var arrayList: ArrayList<GetUserModel> = mList

    val reqptions = RequestOptions()
        .error(R.drawable.ic_person).fallback(R.drawable.ic_person)
        .placeholder(R.drawable.ic_person)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = itemList[position]
        holder.bind(model)
//        holder.setItemOnClickListener(object : ItemClickListener {
//            override fun onItemClick(i: Int) {
//                val bundle = bundleOf("parcel" to model)
//                try {
//                    (mContext as MembersFragment).mNavController.navigate(R.id.action_nav_members_to_editContactFragment, bundle)
//                }    catch (e:Exception){}
//            }
//        })
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun addAll(news: List<GetUserModel?>) {
        val initialSize: Int = itemList.size
        mList.addAll(itemList)
        notifyItemRangeInserted(initialSize, news.size)
    }

    inner class ViewHolder(binding: ItemUsersBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        var itemClickListener: ItemClickListener? = null
        private var name = binding.username
        private var number = binding.mobile
        private var installdate = binding.dateistalled


        init {
//            itemView.setOnClickListener(this)
        }

        fun bind(model: GetUserModel) {
            if (model.timestamp != null) installdate.text = "Date : "  + model.getdateasformatted(model.timestamp!!)
            if (model.name != null) name.text= "Name : "+ model.name
            if (model.mobile != null) number.text= "Number : "+  model.name

        }

        override fun onClick(p0: View?) {
            itemClickListener?.onItemClick(absoluteAdapterPosition)
        }

        fun setItemOnClickListener(itemClickListener: ItemClickListener) {
            this.itemClickListener = itemClickListener
        }
    }

    ///For Search Option
    override fun getFilter(): Filter {
        if (filter == null) {
            filter = CustomFilter()
        }
        return filter as CustomFilter
    }

    internal inner class CustomFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            var constraint = constraint

            val results = FilterResults()
            if (constraint != null && constraint.isNotEmpty()) {
                constraint = constraint.toString().toUpperCase(Locale.ROOT)
                val filteredlist = ArrayList<GetUserModel>()
                for (i in arrayList.indices) {
                    Log.d("performFiltering", "arraylist Item : ${arrayList[i].name}")
                    if (arrayList[i].name?.toUpperCase(Locale.ROOT)?.contains(constraint) == true
                            ) {
                        val l = arrayList[i]
                        filteredlist.add(l)
                        Log.d("performFiltering", "msg: ${l.name}")
                    }
                }

                results.count = filteredlist.size
                results.values = filteredlist

            } else {
                results.count = arrayList.size
                results.values = arrayList
                Log.d("performFiltering", "arraylistsize ${arrayList.size}")
                //Toast.makeText(mContext, "Performing results", Toast.LENGTH_SHORT).show()
            }
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
//            itemList.clear()
            itemList = (results.values as? ArrayList<GetUserModel>)!!

            //   itemList.addAll(results.values as ArrayList<GetUserModel>)
            Log.d("Publishedresults", "publishResults: ${itemList.size} : ")
            notifyDataSetChanged()
            Log.d("publishResults", "msg")
        }
    }
}