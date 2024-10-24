package com.muratguzel.trackyourtime.data.repo

import com.muratguzel.trackyourtime.data.dataSource.AuthDataSource
import com.muratguzel.trackyourtime.data.entitiy.Users

class AuthRepository(val ads: AuthDataSource) {

    suspend fun registerUser(userName: String, email: String, password: String): Boolean =
        ads.registerUser(userName, email, password)

    suspend fun loginUser(email: String, password: String): Boolean = ads.loginUser(email, password)
    suspend fun signOut(): Boolean = ads.signOut()
    suspend fun currentUserNavigate(): Boolean = ads.currentUserNavigate()
    suspend fun getUserData(): Users = ads.getUserData()
}