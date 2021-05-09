package com.example.coco

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val sendBtn = findViewById<ImageButton>(R.id.sendButton)
        val sendText = findViewById<EditText>(R.id.sendText)

        val items = mutableListOf<ListViewItem>()
        val listView = findViewById<ListView>(R.id.listView)

        items.add(ListViewItem("배가 아파", "user", "10:00"))
        items.add(ListViewItem("답변", "coco", "10:01"))
        items.add(ListViewItem("응답", "user", "10:10"))

        var adapter = ListViewAdapter(items)
        listView.adapter = adapter

        sendBtn.setOnClickListener() {
            // 채팅 전송
            val content = sendText.text.toString();

            if (content != "") {
                // 메세지가 입력 된 경우에만 작동
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val formatted = current.format(formatter)

                items.add(ListViewItem(content, "user", formatted))
                items.add(ListViewItem(content+"에 대한 응답", "coco", formatted))
                adapter = ListViewAdapter(items)
                listView.adapter = adapter
            }
        }
    }
}