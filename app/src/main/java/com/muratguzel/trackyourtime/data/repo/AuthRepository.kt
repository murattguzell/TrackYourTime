package com.muratguzel.trackyourtime.data.repo

import com.muratguzel.trackyourtime.data.dataSource.AuthDataSource

class AuthRepository {
    var ads = AuthDataSource()
    suspend fun registerUser(userName: String, email: String, password: String): Boolean =
        ads.registerUser(userName, email, password)

    suspend fun loginUser(email: String, password: String): Boolean = ads.loginUser(email, password)
}