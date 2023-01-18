package com.qure.presenation.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

class SnackBar {
    companion object {
        fun show(view: View, text: String) = Snackbar.make(
            view,
            text,
            Snackbar.LENGTH_SHORT,
        ).show()
    }
}
