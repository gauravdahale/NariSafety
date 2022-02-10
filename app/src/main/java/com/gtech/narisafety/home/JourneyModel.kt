package com.gtech.narisafety.home

import com.google.firebase.database.ServerValue

class JourneyModel {
    var key: String? = null
    var start: String? = null
    var end: String? = null
    var timing: String? = null
    var sosnumber: String? = null
    var middle: ArrayList<String>? = null
    var timestamp = ServerValue.TIMESTAMP

}
class GetJourneyModel {
    var key: String? = null
    var start: String? = null
    var end: String? = null
    var timing: String? = null
    var sosnumber: String? = null
    var middle: ArrayList<String>? = null
    var timestamp :Long?=null

}