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
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.data.entitiy.Users
import com.muratguzel.trackyourtime.data.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AuthViewModel @Inject constructor(val arepo: AuthRepository, application: Application) : AndroidViewModel(application) {
    var registerStatus = MutableLiveData<Boolean>()
    var loginStatus = MutableLiveData<Boolean>()
    var signOutState = MutableLiveData<Boolean>()
    var currentUserNavigateState = MutableLiveData<Boolean>()
    var userData = MutableLiveData<Users>()
    val storage = Firebase.storage
    val mFirestore = Firebase.firestore



    init {
        currentUserNavigate()
        getUserData()
    }

    fun registerUser(userName: String, email: String, password: String) {


        viewModelScope.launch {
            registerStatus.value = arepo.registerUser(userName, email, password)
            Log.e("AuthViewModel", "${registerStatus.value}")
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            loginStatus.value = arepo.loginUser(email, password)
            Log.e("AuthViewModel", "${loginStatus.value}")
        }

    }


    fun signOut() {
        viewModelScope.launch {
            signOutState.value = arepo.signOut()
        }
    }

    fun currentUserNavigate() {
        viewModelScope.launch {
            currentUserNavigateState.value = arepo.currentUserNavigate()
        }
    }

    fun getUserData() {
        viewModelScope.launch {
            userData.value = arepo.getUserData()
        }
    }

    fun uploadProfileImage(users: Users, profilPhotoUri: Uri, context: Context) {
        //profile image upload
        if (profilPhotoUri != null) {
            val dialogBinding = LayoutInflater.from(getApplication())
                .inflate(R.layout.profile_foto_update_dialog, null)
            val myDialog = Dialog(context)
            myDialog.setContentView(dialogBinding)
            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            myDialog.show()

            storage.reference.child("users").child(userData.value!!.userId!!)
                .child(profilPhotoUri.lastPathSegment!!).putFile(profilPhotoUri)
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        Toast.makeText(
                            getApplication(), "Resim Yüklendii ${result.result}",
                            Toast.LENGTH_SHORT
                        ).show()

                        myDialog.dismiss()
                        mFirestore.collection("users").document(userData!!.value!!.userId!!)
                            .update("profileImage", profilPhotoUri)

                    }
                }.addOnFailureListener { exception ->
                    Log.e("RegisterViewModel", exception.localizedMessage!!)
                }
        }
    }
}