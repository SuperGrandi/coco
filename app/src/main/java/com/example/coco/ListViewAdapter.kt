package com.example.coco

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListAdapter
import android.widget.TextView

class ListViewAdapter(private val items: MutableList<ListViewItem>): BaseAdapter(), ListAdapter {
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

            // 전화하기 버튼 출력
            if (item.content.length < 25) {
                call_btn.setVisibility(View.GONE)
            }

            if (item.content.indexOf("지도") !== -1) {
                TODO("지도 호출하기")
            }
        }

        chat_content.text = item.content
        chat_timestamp.text = item.timestamp

        return convertView
    }
}