package com.example.coco.lockscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.coco.R
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.lock_screen.*
import java.util.*

public class lock_Activity : AppCompatActivity() {
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
}