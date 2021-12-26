package com.example.narisafetyadmin.ui.news

import android.text.format.DateFormat
import com.google.firebase.database.ServerValue
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class NewsModel:Serializable {
    var key: String? = null
    var heading: String? = null
    var image: String? = null
    var description: String? = null
    var date: Long? = null
    fun getdateasformatted(date: Long): String {

        val formatter = SimpleDateFormat("dd-mm-yyyy")
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = date
        val fdate: String = DateFormat.format("hh:mm:aa dd-MMM-yyyy ", cal).toString()
        return fdate
    }

}
class AddNewsModel:Serializable {
    var heading: String? = null
    var username :String?=null
    var image: String? = null
    var description: String? = null
    var date = ServerValue.TIMESTAMP

    fun getdateasformatted(date: Long): String {

        val formatter = SimpleDateFormat("dd-mm-yyyy")
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = date
        val fdate: String = DateFormat.format("hh:mm:aa dd-MMM-yyyy ", cal).toString()
        return fdate
    }

}
