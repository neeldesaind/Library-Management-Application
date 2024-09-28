package com.minorproject.librarymanagementapp

import android.provider.ContactsContract.CommonDataKinds.Email
import com.google.firebase.auth.EmailAuthProvider

data class User(
    val name: String? = null,
    val sid: String? = null,
    val gender: String? = null,
    val course: String? = null,
    val email: String? = null,
    val issued: String? = null,
    val fcmToken: String? = null
)
