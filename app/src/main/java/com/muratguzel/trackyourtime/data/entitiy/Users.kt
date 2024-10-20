package com.muratguzel.trackyourtime.data.entitiy

import java.io.Serializable

data class Users(
    var userId: String?=null,
    var fullName: String? = null,
    var email:String?= null,
    var profileImage: String? = null,
    var password: String? = null

):Serializable{
    override fun toString(): String {
        return "Users(userId=$userId, fullName=$fullName, email=$email, profileImage=$profileImage, password=$password)"
    }
}