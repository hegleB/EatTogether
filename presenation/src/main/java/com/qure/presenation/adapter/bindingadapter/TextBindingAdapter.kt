package com.qure.presenation.adapter.bindingadapter

import android.animation.ValueAnimator
import android.os.Handler
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.yy.mobile.rollingtextview.CharOrder
import com.yy.mobile.rollingtextview.RollingTextView
import com.yy.mobile.rollingtextview.strategy.Direction
import com.yy.mobile.rollingtextview.strategy.Strategy

object TextBindingAdapter {

    @BindingAdapter("rollingText")
    @JvmStatic
    fun rollingTextCount(rollingTextView : RollingTextView, count : String?) {
        val animator = ValueAnimator.ofInt(0, 0)
        rollingTextView.animationDuration = 1000L
        rollingTextView.charStrategy = Strategy.CarryBitAnimation(Direction.SCROLL_UP)
        rollingTextView.addCharOrder(CharOrder.Number)
        rollingTextView.animationInterpolator =
            AccelerateDecelerateInterpolator()
        animator.animatedFraction
        animator.addUpdateListener { animation -> rollingTextView.setText(animation.animatedValue.toString()) }
        animator.start()
        val handler = Handler()
        handler.postDelayed({
            animator.cancel()
            rollingTextView.setText(count?:"0")
        }, 0)
    }

    @BindingAdapter("countBarcode")
    @JvmStatic
    fun countBarcode(textView : TextView, l : Long?) {
        textView.setText(String.format("%02d",l))
    }

    @BindingAdapter("barcodeTimeRemaining")
    @JvmStatic
    fun barcodeTimeRemaining(textView : TextView, l : Long?) {
        val time = l?:0
        val hour = time / 3600000L
        val minues = time % 3600000 / 60000
        val second = time % 3600000 % 60000 / 1000
        var scan_time = ""
        scan_time += hour.toString() + ":"
        if (minues < 10) scan_time += "0"
        scan_time += minues.toString() + ":"
        if (second < 10) scan_time += "0"
        scan_time += second
        textView.setText(scan_time)
    }

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
}