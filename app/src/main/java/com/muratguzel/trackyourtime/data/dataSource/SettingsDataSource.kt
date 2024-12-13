package com.muratguzel.trackyourtime.data.dataSource

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratguzel.trackyourtime.data.entitiy.Users
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class SettingsDataSource @Inject constructor(
    val mAuth: FirebaseAuth,
    val mFireStore: FirebaseFirestore,
) {



    suspend fun updateFullName(user: Users, newFullName: String)=
        suspendCancellableCoroutine { continuation ->
            if (user.fullName != newFullName) {
                mFireStore.collection("users").document(user.userId!!)
                    .update("fullName", newFullName).addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            continuation.resume(true)

                        } else {
                            continuation.resume(false)

                        }
                    }
            }

        }

    suspend fun updateEmail(
        user: Users,
        currentEmail: String,
        newEmail: String,
        currentPassword: String,
    ): Boolean = suspendCancellableCoroutine { continuation ->
        if (currentEmail != newEmail) {

            if (!currentEmail.isNullOrEmpty() || !currentPassword.isNullOrEmpty()) {
                mAuth.signInWithEmailAndPassword(currentEmail, currentPassword)
                    .addOnCompleteListener { signInTesk ->
                        if (signInTesk.isSuccessful) {
                            mAuth.currentUser?.verifyBeforeUpdateEmail(newEmail)
                                ?.addOnCompleteListener { updateTask ->
                                    continuation.resume(true)
                                    if (updateTask.isSuccessful) {
                                        if (mAuth.currentUser?.isEmailVerified == true) {
                                            mFireStore.collection("users").document(user.userId!!)
                                                .update("email", newEmail).addOnSuccessListener {

                                                }
                                        }

                                    } else {

                                        Log.e(
                                            "Email Update",
                                            "Failed to send verification email.",

                                            updateTask.exception
                                        )
                                    }
                                }
                        } else {


                        }
                    }
            }
        }
    }

    suspend fun updatePassword(
        newPassword: String, user: Users,
        currentEmail: String,
        currentPassword: String,
    ): Boolean =
        suspendCancellableCoroutine { continuation ->
            if (!currentEmail.isNullOrEmpty() || !currentPassword.isNullOrEmpty()) {
                mAuth.signInWithEmailAndPassword(currentEmail, currentPassword)
                    .addOnCompleteListener { signInTesk ->
                        if (signInTesk.isSuccessful) {
                            mAuth.currentUser?.updatePassword(newPassword)
                                ?.addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        continuation.resume(true)
                                    } else {
                                        continuation.resume(false)
                                    }
                                }


                        }
                    }


            }
        }


}