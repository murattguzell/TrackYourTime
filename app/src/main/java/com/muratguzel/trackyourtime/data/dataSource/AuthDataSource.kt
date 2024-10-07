package com.muratguzel.trackyourtime.data.dataSource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.muratguzel.trackyourtime.data.entitiy.Users
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthDataSource {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun registerUser(
        userName: String,
        email: String,
        password: String
    ): Boolean = suspendCancellableCoroutine { continuation ->
        if (userName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Log.e("AuthDataSource", "registerUser:failure")
            continuation.resume(false)  // Eğer girişler boşsa false döndür
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = mAuth.currentUser?.uid
                        if (userId != null) {
                            val savedUsers = Users(
                                userId = userId,
                                fullName = userName,
                                email = email,
                                createDate = com.google.firebase.Timestamp.now(),
                                updateDate = null,
                                deleteDate = null,
                            )

                            mFireStore.collection("users").document(userId).set(savedUsers)
                                .addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        Log.e("AuthDataSource", "registerUser:success")
                                        continuation.resume(true)  // Başarılı, true döndür
                                    } else {
                                        Log.e("AuthDataSource", "registerUser:failure", dbTask.exception)
                                        continuation.resume(false)  // Firestore hatası
                                    }
                                }.addOnFailureListener { exception ->
                                    Log.e("AuthDataSource", "registerUser:failure", exception)
                                    continuation.resume(false)  // Firestore hatası
                                }
                        } else {
                            continuation.resume(false)  // Kullanıcı ID'si null, başarısız
                        }
                    } else {
                        continuation.resume(false)  // Firebase auth hatası
                    }
                }.addOnFailureListener { exception ->
                    Log.e("AuthDataSource", "registerUser:failure", exception)
                    continuation.resume(false)  // Firebase auth hatası
                }
        }
    }
  suspend fun loginUser(email: String, password: String) :Boolean = suspendCancellableCoroutine{continuation->

        if (email.isEmpty() || password.isEmpty()) {
            continuation.resume(false)
            Log.e("AuthDataSource", "loginUser:failure - Empty email or password")
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                 continuation.resume(true)

                } else {
                    continuation.resume(false)
                    Log.e("AuthDataSource", "loginUser:failure", task.exception)
                }
            }.addOnFailureListener { exception ->
                continuation.resume(false)
                Log.e("AuthDataSource", "loginUser:failure", exception)
            }

        }
    }


}