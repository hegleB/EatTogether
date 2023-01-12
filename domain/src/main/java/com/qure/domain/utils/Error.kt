package com.qure.domain.utils


import android.util.Log
import com.qure.domain.utils.Constants.TAG

class ErrorMessage {
    companion object {
        fun print(errorMessage: String) =
            Log.e(TAG, errorMessage)
    }
}