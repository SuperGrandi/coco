package com.example.coco

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.SensorManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.coco.lockscreen.service.GpsTracker
import com.example.coco.lockscreen.service.ScreenService
import com.example.coco.lockscreen.service.SensorService
import com.example.coco.lockscreen.util.ButtonUnLock
import kotlinx.android.synthetic.main.lock_screen.*
import kotlinx.android.synthetic.main.main_screen.*
import java.io.IOException
import java.util.*

class lock_Activity : AppCompatActivity() {
    private var str: String? = null

    private var sm: SensorManager? = null
    private var enableFall = false
    private var naksang = false

    private var mContext: Context? = null

    private lateinit var btnOK: Button
    private lateinit var btnHelp: Button

    companion object {
        fun newIntent(context: Context?): Intent {
            return Intent(context, lock_Activity::class.java)
                .apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                }
        }
    }

    //몰입모드(소프트키 제거)
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (hasFocus) {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onAttachedToWindow() {
        window.addFlags(
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        )
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        super.onAttachedToWindow()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContentView(R.layout.lock_screen)

        var tv = findViewById(R.id.lockScreenDate) as TextView

        var cal = Calendar.getInstance()
        val month = (cal.get(Calendar.MONTH) + 1).toString()
        val day = cal.get(Calendar.DATE).toString()
        var day_of_week = cal.get(Calendar.DAY_OF_WEEK)

        var week = ""

        if (day_of_week == 1) {
            week = "(일)"
        } else if (day_of_week == 2) {
            week = "(월)"
        } else if (day_of_week == 3) {
            week = "(화)"
        } else if (day_of_week == 4) {
            week = "(수)"
        } else if (day_of_week == 5) {
            week = "(목)"
        } else if (day_of_week == 6) {
            week = "(금)"
        } else {
            week = "(토)"
        }


        str = month + "월 " + day + "일 " + week

        tv.text = str

    }

    override fun onResume() {
        super.onResume()

        setButtonUnlock()
    }

    private fun setButtonUnlock() {
        swipeUnLockButton.setOnUnlockListenerRight(object : ButtonUnLock.OnUnlockListener {
            override fun onUnlock() {
                val intent = Intent(applicationContext, SensorService::class.java)
                startService(intent)
                finish()
            }
        })
    }


    override fun onBackPressed() {

    }

}
