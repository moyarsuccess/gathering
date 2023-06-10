package com.gathering.android.profile

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.gathering.android.auth.model.User
import com.gathering.android.common.RequestState
import com.gathering.android.common.toUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    fun getUserData(): User {
        val user = firebaseAuth.currentUser
        Log.d("WTF", "${user?.email}")
        return user.toUser()
    }

    fun updateDisplayNameAndPhotoURL(
        user: User,
        photoUrl: String,
        onUpdateDone: (requestState: RequestState) -> Unit
    ) {
        val request = UserProfileChangeRequest.Builder()
            .setPhotoUri(Uri.parse(photoUrl))
            .setDisplayName(user.displayName)
            .build()
        firebaseAuth.currentUser?.updateProfile(request)
            ?.addOnSuccessListener {
                onUpdateDone(RequestState.Success(Unit))
            }
            ?.addOnFailureListener {
                onUpdateDone(RequestState.Failure(it))
            }
    }

    @Suppress("ThrowableNotThrown")
    fun uploadPhoto(bitmap: Bitmap?, onUpdateDone: (requestState: RequestState) -> Unit) {
        val storage = FirebaseStorage.getInstance()

        val uId = firebaseAuth.currentUser?.uid ?: ""
        val storageRef = storage.reference

        val imageRef = storageRef.child("images/$uId.jpg")

        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                onUpdateDone(RequestState.Success(downloadUri))
            } else {
                onUpdateDone(RequestState.Failure(Exception(task.exception)))
            }
        }
    }

    fun updateEmail(
        user: User,
        onUpdateDone: (requestState: RequestState) -> Unit
    ) {
        firebaseAuth.currentUser?.updateEmail(user.email ?: "")
            ?.addOnSuccessListener {
                onUpdateDone(RequestState.Success(Unit))
            }
            ?.addOnFailureListener {
                onUpdateDone(RequestState.Failure(it))
            }
    }
}