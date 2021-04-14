package com.example.coco

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView

class ListViewAdapter(private val items: MutableList<ListViewItem>): BaseAdapter(), ListAdapter {
    override fun getCount(): Int = items.size
    override fun getItem(position: Int): ListViewItem = items[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
        var convertView = view

        if (convertView == null) convertView = LayoutInflater.from(parent?.context).inflate(R.layout.chat_item, parent, false)

        val item: ListViewItem = items[position]
        val chat_content = convertView!!.findViewById<TextView>(R.id.chat_content)
        chat_content.text = item.content + " - "+item.type

        return convertView
    }
}