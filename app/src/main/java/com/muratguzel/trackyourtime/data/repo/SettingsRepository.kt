package com.muratguzel.trackyourtime.data.repo

import com.muratguzel.trackyourtime.data.dataSource.SettingsDataSource
import com.muratguzel.trackyourtime.data.entitiy.Users


class SettingsRepository(val sds: SettingsDataSource) {


    suspend fun updateFullName(users: Users, fullName: String)=
        sds.updateFullName(users, fullName)

    suspend fun updateEmail(
        users: Users,
        currentEmail: String,
        newEmail: String,
        currentPassword: String,
    ): Boolean = sds.updateEmail(users, currentEmail, newEmail, currentPassword)

    suspend fun updatePassword(
        newPassword: String, user: Users,
        currentEmail: String,
        currentPassword: String,
    ): Boolean = sds.updatePassword(
        newPassword, user,
        currentEmail,
        currentPassword
    )

}