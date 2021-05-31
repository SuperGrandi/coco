package com.example.coco

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "FirebaseService"

    // FirebaseInstanceIdService는 이제 사라짐. 이제 이걸 사용함
    override fun onNewToken(token: String) {
        Log.d(TAG, "new Token: $token")

        // 토큰 값을 따로 저장해둔다.
//        val pref = this.getSharedPreferences("token", Context.MODE_PRIVATE)
//        val editor = pref.edit()
//        editor.putString("token", token).apply()
//        editor.commit()
//
//        Log.i("로그: ", "성공적으로 토큰을 저장함")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage!!.from)

        // Notification 메시지를 수신할 경우는
        // remoteMessage.notification?.body!! 여기에 내용이 저장되어있다.
        // Log.d(TAG, "Notification Message Body: " + remoteMessage.notification?.body!!)

        Log.d("Notify", remoteMessage.notification?.body!!)

        val intent = Intent(this, DialogActivity::class.java)
        intent.putExtra("lastlat", "111")
        intent.putExtra("lastlon", "222")
        intent.putExtra("message", remoteMessage.notification?.body!!)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

        if(remoteMessage.data.isNotEmpty()){
            Log.i("바디: ", remoteMessage.data["body"].toString())
            Log.i("타이틀: ", remoteMessage.data["title"].toString())
        }

        else {
            Log.i("수신에러: ", "data가 비어있습니다. 메시지를 수신하지 못했습니다.")
            Log.i("data값: ", remoteMessage.data.toString())
        }
    }
}