package com.minorproject.librarymanagementapp

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle received message here
    }

    override fun onNewToken(token: String) {
        Log.d("token", "new token = $token")
        val sharedPref = getSharedPreferences("fcmToken", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("token", token)
        editor.apply()
    }
}

