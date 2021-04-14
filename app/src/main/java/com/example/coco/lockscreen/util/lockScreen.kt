package com.example.coco.lockscreen.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat.startForegroundService
import com.example.coco.lockscreen.receiver.LockScreenApplication
import com.example.coco.lockscreen.service.LockScreenService

object lockScreen {
    fun active() {
        LockScreenApplication.applicationContext()?.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this, LockScreenService::class.java))
            } else {
                startService(Intent(this, LockScreenService::class.java))
            }
        }
    }

    fun deActivate() {
        LockScreenApplication.applicationContext()?.run {
            stopService(Intent(this, LockScreenService::class.java))
        }
    }

    fun getLockScreenStatus() : Boolean {
        val lockScreenPreferences = LockScreenApplication.applicationContext()?.run {
            getSharedPreferences("LockScreenStatus", Context.MODE_PRIVATE)
        }

        return lockScreenPreferences?.getBoolean("LockScreenStatus", false)!!
    }

    val isActive: Boolean
        get() = LockScreenApplication.applicationContext()?.let {
            isMyServiceRunning(LockScreenService::class.java)
        } ?: kotlin.run {
            false
        }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = LockScreenApplication.applicationContext()?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

}