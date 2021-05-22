package com.example.coco

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

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
            if (item.content.length < 25) {
                call_btn.setVisibility(View.GONE)
            } else {
                call_btn.setOnClickListener {
                    Toast.makeText(context, "Click", Toast.LENGTH_SHORT).show()
                }
            }

            if (item.content.indexOf("지도") == -1) {
                map_img.setVisibility(View.GONE)
            } else {
                map_img.setOnClickListener {
                    val mapIntentUri = Uri.parse("geo:37.550368,127.073875")
                    //val mapIntentUri = Uri.parse("\"http://maps.google.com/maps?q=37.550368,127.073875(sejong)&z=15")
                    val mapIntent = Intent(Intent.ACTION_VIEW, mapIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                }
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