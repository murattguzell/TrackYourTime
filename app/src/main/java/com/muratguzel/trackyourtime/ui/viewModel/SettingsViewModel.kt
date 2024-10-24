package com.muratguzel.trackyourtime.ui.viewModel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muratguzel.trackyourtime.data.entitiy.Users
import com.muratguzel.trackyourtime.data.repo.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SettingsViewModel @Inject constructor(
    var settingsRepository: SettingsRepository
) : ViewModel() {

    var updateStatus = MutableLiveData<Boolean>()
    var updateFullNameStatus = MutableLiveData<Boolean>()
    var updatePasswordStatus = MutableLiveData<Boolean>()


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
