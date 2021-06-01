package com.example.coco

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import com.example.coco.lockscreen.service.SensorService
import com.example.coco.lockscreen.util.OnItemClick
import com.squareup.picasso.Picasso
import org.json.JSONObject


class ListViewAdapter(val context: Context, private val items: MutableList<ListViewItem>, var listener: OnItemClick): BaseAdapter(), ListAdapter {
    private lateinit var mContext:Context
    private lateinit var mCallback: OnItemClick

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): ListViewItem = items[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
        var convertView = view
        var chat_content = view;
        var chat_timestamp = view;
        val item: ListViewItem = items[position]
        var YesOrNo = ""

        if(item.content!!.length > 6)
            YesOrNo = item.content!!.substring(item.content.length - 6, item.content.length)

        Log.d("test", YesOrNo)

        if (item.type == "user") {
            convertView = LayoutInflater.from(parent?.context).inflate(R.layout.user_chat_item, parent, false)
            chat_content = convertView!!.findViewById<TextView>(R.id.user_chat_content)
            chat_timestamp = convertView!!.findViewById<TextView>(R.id.user_chat_timestamp)
        }
        else if(YesOrNo == "있으신가요?") {
            convertView = LayoutInflater.from(parent?.context).inflate(R.layout.coco_chat_yesorno, parent, false)
            chat_content = convertView!!.findViewById<TextView>(R.id.yesornoChat)
            chat_timestamp = convertView!!.findViewById<TextView>(R.id.yesorno_chat_timestamp)

            var btnYes = convertView!!.findViewById<Button>(R.id.btnYes)
            var btnNo = convertView!!.findViewById<Button>(R.id.btnNo)

            mCallback = listener

            btnYes.setOnClickListener {
                mCallback.onClick("예")
            }

            btnNo.setOnClickListener {
                mCallback.onClick("아니요")
            }

        }
        else {
            convertView = LayoutInflater.from(parent?.context).inflate(R.layout.coco_chat_item, parent, false)
            chat_content = convertView!!.findViewById<TextView>(R.id.coco_chat_content)
            chat_timestamp = convertView!!.findViewById<TextView>(R.id.coco_chat_timestamp)

            var call_btn = convertView!!.findViewById<Button>(R.id.call_button)
            var map_img = convertView!!.findViewById<ImageView>(R.id.map_image)

            // 전화하기 버튼 출력
            if (item.tel != null) {
                call_btn.setOnClickListener {
                    //Toast.makeText(context, "Click", Toast.LENGTH_SHORT).show()
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + item.tel))
                    context.startActivity(intent)
                }
            } else {
                call_btn.setVisibility(View.GONE)
            }

            // 지도 출력
            if (item.lat != null && item.lng != null) {
                // 지도 이미지 출력
                val imgUrl = "https://maps.googleapis.com/maps/api/staticmap?center="+item.lat+","+item.lng+"&zoom=17&scale=1&size=600x300&maptype=roadmap&markers=color:red|label:H|"+item.lat+","+item.lng+"&key=AIzaSyBbKJZdXNaZF9QddLZzwtnIZ9_7DDOhDkI&format=png&visual_refresh=true"

                Picasso.get().load(imgUrl).into(map_img)

                map_img.setOnClickListener {
                    val mapIntentUri = Uri.parse("google.navigation:q=" + item.lat + "," + item.lng)
                    val mapIntent = Intent(Intent.ACTION_VIEW, mapIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                }
            } else {
                map_img.setVisibility(View.GONE)
            }

        }

        chat_content.text = item.content
        chat_timestamp.text = item.timestamp

        return convertView
    }
    fun EfficientAdapter(c: Context) {
        mContext = c
    }


}