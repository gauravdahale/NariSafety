package com.gtech.narisafety.models

import com.google.firebase.database.ServerValue

class UserModel {
    var name: String? = null
    var number: String? = null
    var key: String? = null
    var uid: String? = null
    var timestamp = ServerValue.TIMESTAMP
}