package com.example.coco.lockscreen.util

import android.Manifest.permission
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.coco.R
import com.example.coco.lock_Activity
import com.example.coco.lockscreen.service.GpsTracker
import kotlinx.android.synthetic.main.view_unlock.view.*
import java.util.*

class ButtonUnLock : RelativeLayout {

    private var listenerRight: OnUnlockListener? = null
    private var slideButton: FrameLayout? = null
    private var lockedImage: ImageView? = null
    private var thumbWidth = 0
    private var sliding = false
    private var sliderPosition = 220
    private var initialSliderPosition = 0
    private var initialSlidingX = 0f

    var phoneNum = ""
    var textMsg: String? = null
    var prevNum = ""

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    fun setOnUnlockListenerRight(listener: OnUnlockListener) {
        this.listenerRight = listener
    }

    private fun reset() {
        lockedImage?.setImageResource(R.drawable.ic_locked)
        slideButton?.run {
            val params = layoutParams as LayoutParams
            val animator = ValueAnimator.ofInt(params.leftMargin, 220)
            animator.addUpdateListener { valueAnimator ->
                params.leftMargin = valueAnimator.animatedValue as Int
                requestLayout()
            }
            animator.duration = 300
            animator.start()
        }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.view_unlock, this, true)
        // Retrieve layout elements
        slideButton = view.swipeButton
        lockedImage = view.lockedImage

        view.swipeButton.visibility = View.VISIBLE
        view.lockedImage.visibility = View.VISIBLE

        // Get padding
        //thumbWidth = dpToPx(120); // 60dp + 2*10dp

        val viewTreeObserver = this.viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    this@ButtonUnLock.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    slideButton?.run {
                        thumbWidth = slideButton?.width!!
                        if (view.width == 0) {
                            view.swipeButton.visibility = View.INVISIBLE
                            view.lockedImage.visibility = View.INVISIBLE
                            init(context, null)
                        }

                        sliderPosition = 220
                        val params = layoutParams as LayoutParams
                        params.setMargins(220, 0, 0, 0)
                        layoutParams = params
                    }
                }
            })
        }

    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        //처음 눌렸을 때
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (event.x >= sliderPosition && event.x < sliderPosition + thumbWidth) {
                lockedImage?.setImageResource(R.drawable.ic_unlocked)
                sliding = true
                initialSlidingX = event.x
                initialSliderPosition = sliderPosition
            }
        }
        //ACTION_UP 누르고 땠을 때,
        else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_OUTSIDE) {
            //오른쪽으로 끝까지 슬라이드 시 unLock
            if (sliderPosition >= measuredWidth - (thumbWidth + 20)) {
                if (listenerRight != null){
                    listenerRight?.onUnlock()
                }
            }
            //왼쪽으로 끝까지 슬라이드 시 저장된 연락처로 문자
            else if (sliderPosition <= 0) {
                if(listenerRight != null) {
                    val gpsTracker = GpsTracker(context)
                    val latitude = gpsTracker.latitude
                    val longitude = gpsTracker.longitude

                    val smsdBhelper = SMSDBhelper(context);
                    smsdBhelper.open()

                    var itemIds: MutableList<String> = ArrayList()
                    val cursor = smsdBhelper.allContacts

                    cursor.moveToFirst()

                    if (cursor.moveToFirst()) {
                        do {
                            val data = cursor.getString(cursor.getColumnIndex("contact"))
                            itemIds.add(data)
                        } while (cursor.moveToNext())
                    }
                    cursor.close()

                    var it: Iterator<String> = itemIds.iterator()

                    while (it.hasNext()) {
                        phoneNum = it.next()
                        phoneNum = phoneNum.substring(phoneNum.lastIndexOf(":") + 2)
                        textMsg = "사고 발생: " + "http://maps.google.com/?q=" + latitude.toString() + "," + longitude.toString()

                        try {
                            val sms = SmsManager.getDefault()
                            sms.sendTextMessage(phoneNum, null, textMsg, null, null)
                            Toast.makeText(context, "코코에 저장된 연락처로 문자전송 완료!", Toast.LENGTH_LONG).show()
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }

                        Log.d("Message", "$textMsg<$phoneNum>")
                        prevNum = phoneNum
                    }
                    smsdBhelper.close()
                    sliding = false
                    sliderPosition = 220
                    reset()
                }
            }
            else {
                sliding = false
                sliderPosition = 220
                reset()
            }
        }
        //누르고 움직였을 때
        else if (event.action == MotionEvent.ACTION_MOVE && sliding) {
            sliderPosition = (initialSliderPosition + (event.x - initialSlidingX)).toInt()
            if (sliderPosition <= 0) {
                sliderPosition = 0
            }

            if (sliderPosition >= measuredWidth - (thumbWidth)) {
                sliderPosition = measuredWidth - (thumbWidth + 20)
            } else {
                val max = measuredWidth - thumbWidth
            }
            setMarginLeft(sliderPosition)
        }

        return true
    }

    private fun setMarginLeft(margin: Int) {
        slideButton?.run {
            val params = layoutParams as LayoutParams
            params.setMargins(margin, 0, 0, 0)
            layoutParams = params
        } ?: kotlin.run {
            return
        }

    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    interface OnUnlockListener {
        fun onUnlock()
    }
}