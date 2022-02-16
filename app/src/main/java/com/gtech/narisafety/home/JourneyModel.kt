package com.gtech.narisafety.home

import com.google.firebase.database.ServerValue
import java.io.Serializable

class JourneyModel:Serializable {
    var key: String? = null
    var start: String? = null
    var end: String? = null
    var timing: String? = null
    var sosnumber: String? = null
    var middle: ArrayList<String>? = null
    var timestamp = ServerValue.TIMESTAMP

}
class GetJourneyModel:Serializable {
    var key: String? = null
    var start: String? = null
    var end: String? = null
    var timing: String? = null
    var sosnumber: String? = null
    var middle: ArrayList<String>? = null
    var timestamp :Long?=null

}