package com.qure.domain.utils


import android.util.Log

class ErrorMessage {
    companion object {
        fun print(errorMessage: String) =
            Log.e(TAG, errorMessage)
    }
}