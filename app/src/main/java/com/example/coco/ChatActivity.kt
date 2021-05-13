package com.example.coco

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import com.example.coco.lockscreen.service.ScreenService

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val sendBtn = findViewById<ImageButton>(R.id.sendButton)
        val sendText = findViewById<EditText>(R.id.sendText)

        val items = mutableListOf<ListViewItem>()
        val listView = findViewById<ListView>(R.id.listView)

        items.add(ListViewItem("배가 아파", "user"))
        items.add(ListViewItem("답변", "coco"))
        items.add(ListViewItem("응답", "user"))

        var adapter = ListViewAdapter(items)
        listView.adapter = adapter

        sendBtn.setOnClickListener() {
            // 채팅 전송
            val content = sendText.text.toString();
            items.add(ListViewItem(content, "user"))
            items.add(ListViewItem("응답", "coco"))
            adapter = ListViewAdapter(items)
            listView.adapter = adapter
        }

        val intent = Intent(applicationContext, ScreenService::class.java)
        startService(intent)

    }
}