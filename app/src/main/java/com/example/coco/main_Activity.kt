package com.example.coco

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.coco.lockscreen.service.ScreenService

class main_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen)

        val intent = Intent(applicationContext, ScreenService::class.java)
        startService(intent)

        val btn_event = findViewById<Button>(R.id.moveChat)

        btn_event.setOnClickListener {
            val chatPage = Intent(this, ChatActivity::class.java)
            startActivity(chatPage)
        }
    }
}