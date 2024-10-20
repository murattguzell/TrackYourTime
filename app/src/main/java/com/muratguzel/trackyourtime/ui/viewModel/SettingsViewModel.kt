package com.muratguzel.trackyourtime.ui.viewModel

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.data.entitiy.Users
import com.muratguzel.trackyourtime.data.repo.SettingsRepository
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    var settingsRepository = SettingsRepository()
    var updateStatus = MutableLiveData<Boolean>()
    var updateFullNameStatus = MutableLiveData<Boolean>()
    var updatePasswordStatus = MutableLiveData<Boolean>()
    val mStorage: FirebaseStorage = Firebase.storage


    fun updateFullName(users: Users, fullName: String) {
        viewModelScope.launch {
            updateFullNameStatus.value = settingsRepository.updateFullName(users, fullName)
        }
    }

    fun updateEmail(users: Users, currentEmail: String, newEmail: String, currentPassword: String) {
        viewModelScope.launch {
            updateStatus.value =
                settingsRepository.updateEmail(users, currentEmail, newEmail, currentPassword)
        }
    }

    fun updatePassword(
        newPassword: String, user: Users,
        currentEmail: String,
        currentPassword: String,
    ) {
        viewModelScope.launch {
            updatePasswordStatus.value = settingsRepository.updatePassword(
                newPassword, user,
                currentEmail,
                currentPassword,
            )
        }

    }


}
