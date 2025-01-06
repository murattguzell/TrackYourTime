package com.muratguzel.trackyourtime.data.dataSource

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratguzel.trackyourtime.data.entitiy.Users
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class AuthDataSource @Inject constructor(
    private val mAuth: FirebaseAuth,
    private val mFireStore: FirebaseFirestore,
) {


    suspend fun registerUser(
        userName: String,
        email: String,
        password: String,
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
                                password = password
                            )

                            mFireStore.collection("users").document(userId).set(savedUsers)
                                .addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        Log.e("AuthDataSource", "registerUser:success")
                                        continuation.resume(true)  // Başarılı, true döndür
                                    } else {
                                        Log.e(
                                            "AuthDataSource",
                                            "registerUser:failure",
                                            dbTask.exception
                                        )
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

                }
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean =
        suspendCancellableCoroutine { continuation ->

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
                    Log.e("AuthDataSource", "loginUser:failure", exception)
                }

            }
        }

    suspend fun signOut(): Boolean = suspendCancellableCoroutine { continuation ->
        mAuth.signOut()
        continuation.resume(true)
    }

    suspend fun currentUserNavigate(): Boolean = suspendCancellableCoroutine { continuation ->
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            continuation.resume(true)
        } else {
            continuation.resume(false)
        }
    }

    suspend fun getUserData(): Users = suspendCancellableCoroutine { continuation ->
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            mFireStore.collection("users").document(mAuth.currentUser!!.uid)
                .addSnapshotListener { value, error ->
                    if (value != null) {
                        val userData = value.toObject(Users::class.java)
                        if (userData != null) {
                            continuation.takeIf { it.isActive }?.resume(userData)

                        }
                    }
                }
        }


    }
    suspend fun forgotPassword(email: String): Boolean = suspendCancellableCoroutine { continuation ->
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
        if (task.isSuccessful){
            continuation.resume(true)
        }else{
            continuation.resume(false)
        }

        }

    }


}
