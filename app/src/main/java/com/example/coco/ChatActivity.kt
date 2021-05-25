package com.example.coco

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.coco.lockscreen.service.GpsTracker
import com.example.coco.lockscreen.service.ScreenService
import com.example.coco.lockscreen.service.SensorService
import com.example.coco.lockscreen.setting_Activity
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.*


class ChatActivity : AppCompatActivity() {
    // 채팅 관련 변수
    val items = mutableListOf<ListViewItem>()
    private lateinit var listView: ListView;
    private lateinit var adapter: ListViewAdapter

    //permission
    private val PERMISSIONS_REQUEST_CODE = 100
    private var REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECORD_AUDIO
    )
    private var gpsTracker: GpsTracker? = null
    private val GPS_ENABLE_REQUEST_CODE = 2001

    // TTS
    private var tts: TextToSpeech? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        checkRunTimePermission()

        //start service
        var intent = Intent(applicationContext, SensorService::class.java)
        startService(intent)

        intent = Intent(applicationContext, ScreenService::class.java)
        startService(intent)

        // STT 관련 코드
        //requestPermission()
        intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)

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
        items.add(ListViewItem("응답", "coco", "10:10", "37.55634362962125", "125.07976165234159"))

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

                gpsTracker = GpsTracker(this)

                var lat:String = ""
                var lng:String = ""

                if (gpsTracker != null) {
                    lat = gpsTracker!!.getLatitude().toString()
                    lng = gpsTracker!!.getLongitude().toString()
                }

                var responseDatas: Array<String?>

                addMessage(arrayOf(content), "user", parentContext)

                val msgWait = CoroutineScope(Dispatchers.IO).launch {
                    responseDatas = sendMessage(content, lat, lng)

                    addMessage(responseDatas, "coco", parentContext)
                }

                sendText.setText("")
            }
        }
        val btnSetting = findViewById<Button>(R.id.btnSetting)
        btnSetting.setOnClickListener {
            val settingPage = Intent(this, setting_Activity::class.java)
            startActivity(settingPage)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addMessage(datas: Array<String?>, type: String, context: Context) {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val formatted = current.format(formatter)

        var message = datas[0]

        runOnUiThread {
            if (datas.size > 1) {
                items.add(ListViewItem(message, type, formatted, datas[1], datas[2], datas[3]))
            } else {
                items.add(ListViewItem(message, type, formatted))
            }

            adapter = ListViewAdapter(context, items)
            listView.adapter = adapter

            if (type == "coco") {
                ttsSpeak(message)
            }

            listView.setSelection(adapter.count - 1);

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
                var parentContext = this

                var responseDatas: Array<String?>

                addMessage(arrayOf(result), "user", parentContext)

                var lat:String = ""
                var lng:String = ""

                if (gpsTracker != null) {
                    lat = gpsTracker!!.getLatitude().toString()
                    lng = gpsTracker!!.getLongitude().toString()
                }

                val msgWait = CoroutineScope(Dispatchers.IO).launch {
                    responseDatas = sendMessage(result, lat, lng)

                    addMessage(responseDatas, "coco", parentContext)
                }

                val sendText = findViewById<EditText>(R.id.etMSG)

                sendText.setText("")
            }
        }
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE ->
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음")
                        checkRunTimePermission()
                        return
                    }
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
            Toast.makeText(this, "Result : " + it.toString(), Toast.LENGTH_SHORT).show()
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
    private fun ttsSpeak(strTTS: String?):Boolean {
        if (strTTS == null)
            return false
        tts?.speak(strTTS, TextToSpeech.QUEUE_ADD, null, null)
        return true
    }

    // 메세지 전송
    private suspend fun sendMessage(message: String, uLat: String, uLng: String): Array<String?> {

        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()

        // Create Retrofit
        val retrofit = Retrofit.Builder()
                .baseUrl("https://n3ase4t7k2.execute-api.ap-northeast-2.amazonaws.com")
                .client(okHttpClient)
                .build()

        // Create Service
        val service = retrofit.create(MessageService::class.java)

        // Create JSON using JSONObject
        val jsonObject = JSONObject()
        jsonObject.put("session_id", "test")
        jsonObject.put("message", message)
//        jsonObject.put("latitude", uLat)
//        jsonObject.put("longitude", uLng)
        jsonObject.put("latitude", "37.4884")
        jsonObject.put("longitude", "127.1288")

        Log.d("lat :", uLat)
        Log.d("lng :", uLng)

        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        var resultMsg:String = ""
        var lat:String? = null
        var lng:String? = null
        var tel:String? = null

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
                    if (result.hospital_info != null) {
                        result.hospital_info.tel_num
                        lat = result.hospital_info.latitude
                        lng = result.hospital_info.longitude
                        tel = result.hospital_info.tel_num
                    }

                } else {
                    Log.e("RETROFIT_ERROR", response.code().toString())
                    resultMsg = "ERR"
                }
            }
        }

        netScope.join()

        Log.d("after coroutine", resultMsg)

        netScope.cancel()

        return arrayOf(resultMsg, lat, lng, tel)
    }

    override fun onRequestPermissionsResult(permsRequestCode: Int, permissions: Array<String?>, grandResults: IntArray) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.size == REQUIRED_PERMISSIONS.size) {
            var check_result = true

            for (result in grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }
            if (check_result) {

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[3])
                ) {
                    Toast.makeText(this@ChatActivity, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@ChatActivity, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                this@ChatActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
                this@ChatActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)

            // 3.  위치 값을 가져올 수 있음
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@ChatActivity, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(this@ChatActivity, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show()
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(this@ChatActivity, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(this@ChatActivity, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
            }
        }
    }

    private fun showDialogForLocationServiceSetting() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@ChatActivity)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("""앱을 사용하기 위해서는 위치 서비스가 필요합니다.위치 설정을 수정하실래요?""".trimIndent())
        builder.setCancelable(true)
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialog, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        })
        builder.setNegativeButton(
                "취소",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        builder.create().show()
    }



    fun checkLocationServicesStatus(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    fun getCurrentAddress(latitude: Double, longitude: Double): String {
        //지오코더... GPS를 주소로 변환
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>
        addresses = try {
            geocoder.getFromLocation(latitude, longitude, 7)
        } catch (ioException: IOException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            return "지오코더 서비스 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return "잘못된 GPS 좌표"
        }
        if (addresses == null || addresses.size == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show()
            return "주소 미발견"
        }
        val address: Address = addresses[0]
        return address.getAddressLine(0).toString().toString() + "\n"
    }
}