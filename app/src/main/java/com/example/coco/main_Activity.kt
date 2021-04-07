package com.example.coco

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class main_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen)

        val btn_event = findViewById<Button>(R.id.moveChat)

        btn_event.setOnClickListener {
            val chatPage = Intent(this, Chat_Activity::class.java)
            startActivity(chatPage)
        }
    }
}