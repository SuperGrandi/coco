package com.example.coco

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.coco.lockscreen.service.ScreenService
import com.example.coco.lockscreen.service.SensorService
import com.example.coco.lockscreen.setting_Activity

class main_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen)


        var intent = Intent(applicationContext, SensorService::class.java)
        startService(intent)

        intent = Intent(applicationContext, ScreenService::class.java)
        startService(intent)

        val btn_event = findViewById<Button>(R.id.moveChat)

        btn_event.setOnClickListener {
            val chatPage = Intent(this, ChatActivity::class.java)
            startActivity(chatPage)
        }

       val btnSetting = findViewById<Button>(R.id.btnSetting)
        btnSetting.setOnClickListener {
            val settingPage = Intent(this, setting_Activity::class.java)
            startActivity(settingPage)
        }
    }
}