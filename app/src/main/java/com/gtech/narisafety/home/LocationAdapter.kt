package com.gtech.narisafety.home

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gtech.narisafety.R

class LocationAdapter(var locatlionlist: ArrayList<String>):RecyclerView.Adapter<LocationHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location,parent,false)
    return  LocationHolder(view)
    }

    override fun onBindViewHolder(holder: LocationHolder, position: Int) {
    val md = locatlionlist[position]
        holder.title.setText(md!!)
    }

    override fun getItemCount(): Int {
        return  locatlionlist.size
    }

}

class LocationHolder(view: View):RecyclerView.ViewHolder(view) {
var title = view.findViewById<TextView>(R.id.item_location)
}
