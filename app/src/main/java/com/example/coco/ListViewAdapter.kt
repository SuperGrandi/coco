package com.example.coco

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import com.squareup.picasso.Picasso


class ListViewAdapter(val context: Context, private val items: MutableList<ListViewItem>): BaseAdapter(), ListAdapter {
    private lateinit var mContext:Context

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): ListViewItem = items[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
        var convertView = view
        var chat_content = view;
        var chat_timestamp = view;
        val item: ListViewItem = items[position]

        if (item.type == "user") {
            convertView = LayoutInflater.from(parent?.context).inflate(R.layout.user_chat_item, parent, false)
            chat_content = convertView!!.findViewById<TextView>(R.id.user_chat_content)
            chat_timestamp = convertView!!.findViewById<TextView>(R.id.user_chat_timestamp)
        } else {
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
                val imgUrl = "https://maps.googleapis.com/maps/api/staticmap?center="+item.lat+","+item.lng+"&zoom=16&scale=1&size=600x300&maptype=roadmap&key=AIzaSyBbKJZdXNaZF9QddLZzwtnIZ9_7DDOhDkI&format=png&visual_refresh=true"

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