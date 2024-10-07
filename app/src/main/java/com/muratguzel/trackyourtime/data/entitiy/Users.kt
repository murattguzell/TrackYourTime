package com.muratguzel.trackyourtime.data.entitiy

import com.google.firebase.Timestamp

data class Users(
    var userId: String?=null,
    var fullName: String? = null,
    var email:String?= null,
    var createDate: Timestamp?=null,
    var updateDate: Timestamp?=null,
    var deleteDate: Timestamp?=null,
    var active:Boolean = false,
){

    override fun toString(): String {
        return "Users(userId=$userId, fullName=$fullName, email=$email, createDate=$createDate, updateDate=$updateDate, deleteDate=$deleteDate, active=$active)"
    }
}