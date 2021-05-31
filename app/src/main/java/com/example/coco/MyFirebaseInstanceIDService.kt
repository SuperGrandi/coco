package com.example.coco

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId

class MyFirebaseInstanceIDService {
    fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d("Token", "Refreshed token: $refreshedToken")
    }
}