package com.example.coco

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ChatActivity : AppCompatActivity() {
    // 채팅 관련 변수
    val items = mutableListOf<ListViewItem>()
    private lateinit var listView: ListView;
    private lateinit var adapter: ListViewAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // STT 관련 코드
        requestPermission()
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5)

        val speechBtn = findViewById<ImageButton>(R.id.speechButton)

        // 음성인식 버튼 클릭 이벤
        speechBtn.setOnClickListener {
            startActivityForResult(intent, 10)
        }

        // Chat 관련 코드
        val sendBtn = findViewById<ImageButton>(R.id.sendButton)
        val sendText = findViewById<EditText>(R.id.sendText)

        listView = findViewById<ListView>(R.id.listView)

        items.add(ListViewItem("배가 아파", "user", "10:00"))
        items.add(ListViewItem("답변", "coco", "10:01"))
        items.add(ListViewItem("응답", "user", "10:10"))

        adapter = ListViewAdapter(items)
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
                items.add(ListViewItem(content + "에 대한 응답", "coco", formatted))
                adapter = ListViewAdapter(items)
                listView.adapter = adapter
            }
        }
    }

    // 음성인식 결과 처리하기
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 10) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!![0]

            if (result != "") {
                // 메세지가 입력 된 경우에만 작동
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val formatted = current.format(formatter)

                items.add(ListViewItem(result, "user", formatted))
                items.add(ListViewItem(result + "에 대한 응답", "coco", formatted))
                adapter = ListViewAdapter(items)
                listView.adapter = adapter
            }
        }
    }

    // 마이크 권한 요청하기
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO), 0
            )
        }

    }
}