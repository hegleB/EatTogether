package com.qure.presenation.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.WindowManager

class ResizeDialog(val context: Context, val dialog: Dialog) {

    fun setUpDialogFragment() {
        val size = Point()
        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.MATCH_PARENT

        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager?.defaultDisplay
            display?.getSize(size)
            val window = dialog?.window
            window?.setLayout(width, height)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        } else {
            val rect = windowManager?.currentWindowMetrics?.bounds
            val window = dialog?.window

            if (rect != null) {
                window?.setLayout(width, height)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }
}