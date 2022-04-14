package com.qure.eattogether2.bindingadapter

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

import java.text.SimpleDateFormat
import java.util.*

object TextBindingAdapter {

    @BindingAdapter("timeText")
    @JvmStatic
    fun timeText(textView: TextView, createTime: String) {

        val SEC = 60
        val MIN = 60
        val HOUR = 24
        val DAY = 30
        val MONTH = 12
        val current = System.currentTimeMillis()

        var diffTime = (current - createTime.toLong()) / 1000

        if (diffTime < SEC) {
            textView.setText("방금 전")
        } else if (SEC.let { diffTime /= it; diffTime } < MIN) {
            textView.setText(diffTime.toString() + "분 전")
        } else if (MIN.let { diffTime /= it; diffTime } < HOUR) {
            textView.setText(diffTime.toString() + "시간 전")
        } else if (HOUR.let { diffTime /= it; diffTime } < DAY) {
            textView.setText(diffTime.toString() + "일 전")
        } else if (DAY.let { diffTime /= it; diffTime } < MONTH) {
            textView.setText(diffTime.toString() + "개월 전")
        }
    }

    @BindingAdapter("chattimeText")
    @JvmStatic
    fun chatTimeText(textView: TextView, chatTime: String) {

        if (!chatTime.equals("")) {
            val time: Long = chatTime.toLong()
            val t_date = Date(time)
            val t_dateFormat = SimpleDateFormat("aa hh:mm", Locale("ko", "KR"))
            val str_date = t_dateFormat.format(t_date)
            textView.setText(str_date)
        }
    }

    @BindingAdapter("chatRoomCountText")
    @JvmStatic
    fun chatRoomCount(textView: TextView, chatroomCount : Int){

        if(chatroomCount==2){
            textView.visibility = View.INVISIBLE
        } else {
            textView.visibility = View.VISIBLE
            textView.setText(chatroomCount.toString())
        }
    }

    
}