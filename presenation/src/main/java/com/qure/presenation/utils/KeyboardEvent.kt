package com.qure.presenation.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager

class KeyboardEvent(val context: Context) {

    fun openKeyboard() {
        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }


    fun hideKeyboard() {
        val immhide = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }
}