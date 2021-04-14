package com.example.coco.lockscreen

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.coco.R
import com.example.coco.lockscreen.util.ButtonUnLock
import com.example.coco.lockscreen.util.ViewUnLock
import kotlinx.android.synthetic.main.lock_screen.*
import java.util.*

public class lock_Activity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context?) : Intent {
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
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        )
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        super.onAttachedToWindow()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lock_screen)

        var tv = findViewById(R.id.lockScreenDate) as TextView

        var cal = Calendar.getInstance()
        val month = (cal.get(Calendar.MONTH) + 1).toString()
        val day = cal.get(Calendar.DATE).toString()
        var day_of_week = cal.get(Calendar.DAY_OF_WEEK)

        var week = ""

        if(day_of_week == 1)
            week = "(일)"
        else if(day_of_week == 2)
            week = "(월)"
        else if(day_of_week == 3)
            week = "(화)"
        else if(day_of_week == 4)
            week = "(수)"
        else if(day_of_week == 5)
            week = "(목)"
        else if(day_of_week == 6)
            week = "(금)"
        else
            week = "(토)"

        var str = month + "월 " + day + "일 " + week

        tv.setText(str)
    }

    override fun onResume() {
        super.onResume()

        setButtonUnlock()
        setViewUnlock()
    }

    private fun setButtonUnlock() {
        swipeUnLockButton.setOnUnlockListenerRight(object : ButtonUnLock.OnUnlockListener {
            override fun onUnlock() {
                finish()
            }
        })
    }


    private fun setViewUnlock() {
        lockScreenView.x = 0f
        lockScreenView.setOnTouchListener(object : ViewUnLock(this, lockScreenView) {
            override fun onFinish() {
                finish()
                super.onFinish()
            }
        })
    }

    override fun onBackPressed() {

    }
}