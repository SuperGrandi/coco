package com.example.coco

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import retrofit2.Retrofit;
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlin.coroutines.*
import kotlinx.coroutines.*

class ChatActivity : AppCompatActivity() {
    // 채팅 관련 변수
    val items = mutableListOf<ListViewItem>()
    private lateinit var listView: ListView;
    private lateinit var adapter: ListViewAdapter

    // TTS
    private var tts: TextToSpeech? = null

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

        // TTS 생성
        initTextToSpeech()

        val speechBtn = findViewById<ImageButton>(R.id.speechButton)

        // 음성인식 버튼 클릭 이벤
        speechBtn.setOnClickListener {
            startActivityForResult(intent, 10)
        }

        // Chat 관련 코드
        val sendBtn = findViewById<ImageButton>(R.id.btnSend)
        val sendText = findViewById<EditText>(R.id.etMSG)

        listView = findViewById<ListView>(R.id.lvChat)

        items.add(ListViewItem("배가 아파", "user", "10:00"))
        items.add(ListViewItem("답변", "coco", "10:01"))
        items.add(ListViewItem("응답", "user", "10:10"))

        adapter = ListViewAdapter(this, items)
        listView.adapter = adapter

        sendBtn.setOnClickListener() {
            // 채팅 전송
            val content = sendText.text.toString();

            if (content != "") {
                // 메세지가 입력 된 경우에만 작동
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val formatted = current.format(formatter)
                var parentContext = this

                var responseMsg = ""

                addMessage(content, "user", parentContext)


                val msgWait = CoroutineScope(Dispatchers.IO).launch {
                    responseMsg = sendMessage(content)

                    addMessage(responseMsg, "coco", parentContext)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addMessage(message:String, type:String, context: Context) {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val formatted = current.format(formatter)

        runOnUiThread {
            items.add(ListViewItem(message, type, formatted))
            adapter = ListViewAdapter(context, items)
            listView.adapter = adapter

            if (type == "coco") {
                ttsSpeak(message)
            }
        }
    }

    // 음성인식 결과 처리하기
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 10) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!![0]

            if (result != "") {
                // 메세지가 입력 된 경우에만 작동
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val formatted = current.format(formatter)

                items.add(ListViewItem(result, "user", formatted))
                items.add(ListViewItem(result + "에 대한 응답", "coco", formatted))
                adapter = ListViewAdapter(this, items)
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

    // TTS 생성
    private fun initTextToSpeech() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(this, "SDK Version Low", Toast.LENGTH_SHORT).show()
            return
        }

        tts = TextToSpeech(this) {
            Toast.makeText(this, "Result : "+it.toString(), Toast.LENGTH_SHORT).show()
            if (it == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.KOREAN)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "지원하지 않는 언어", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "TTS 설정 완료", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "TTS 생성 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // TTS 호출
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun ttsSpeak(strTTS: String) {
        tts?.speak(strTTS, TextToSpeech.QUEUE_ADD, null, null)
    }

    // 메세지 전송
    private suspend fun sendMessage(message: String):String {

        // Create Retrofit
        val retrofit = Retrofit.Builder()
                .baseUrl("https://n3ase4t7k2.execute-api.ap-northeast-2.amazonaws.com")
                .build()

        // Create Service
        val service = retrofit.create(MessageService::class.java)

        // Create JSON using JSONObject
        val jsonObject = JSONObject()
        jsonObject.put("session_id", "test")
        jsonObject.put("message", message)

        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        var resultMsg:String = ""

       val netScope = CoroutineScope(Dispatchers.IO).launch {
            // Do the POST request and get response
            val response = service.sendMessage(requestBody)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                            JsonParser.parseString(

                                    response.body()
                                            ?.string()
                            )
                    )

                    val result = gson.fromJson(prettyJson, MessageData::class.java)

                    Log.d("Result :", result.message)

                    resultMsg = result.message
                } else {
                    Log.e("RETROFIT_ERROR", response.code().toString())
                    resultMsg = "ERR"
                }
            }
        }

        netScope.join()

        Log.d("after coroutine", resultMsg)

        netScope.cancel()

        return resultMsg
    }
}