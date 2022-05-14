package com.gtech.narisafety.notification

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gtech.narisafety.R
import com.gtech.narisafety.databinding.FragmentNotificationBinding

import com.gtech.narisafety.notification.placeholder.PlaceholderContent.PlaceholderItem

class MyNotificationsAdapter(
    private val values: ArrayList<GetNotification>
) : RecyclerView.Adapter<MyNotificationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = (position+1).toString()
        holder.contentView.text = item.msg
        holder.sentby.text = item.sentby
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content
        val sentby: TextView = binding.sentbynumber

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}