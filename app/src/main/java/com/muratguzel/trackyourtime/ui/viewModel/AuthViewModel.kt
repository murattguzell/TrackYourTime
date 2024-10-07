package com.muratguzel.trackyourtime.ui.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.muratguzel.trackyourtime.data.repo.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val mAuth = Firebase.auth
    var registerStatus = MutableLiveData<Boolean>()
    var loginStatus = MutableLiveData<Boolean>()
    var signOutState = MutableLiveData<Boolean>()


    fun registerUser(userName: String, email: String, password: String) {

       var  arepo = AuthRepository()

        viewModelScope.launch {
            registerStatus.value = arepo.registerUser(userName, email, password)
            Log.e("AuthViewModel","${registerStatus.value}")
        }
    }
    fun loginUser(email: String, password: String) {
        var mrepo = AuthRepository()
        viewModelScope.launch {
            loginStatus.value = mrepo.loginUser(email, password)
            Log.e("AuthViewModel","${loginStatus.value}")
        }

    }


    fun signOut() {
        mAuth.signOut()
        signOutState.value = true
    }
}