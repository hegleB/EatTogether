package com.qure.presenation.utils

import android.app.Activity
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController

class OnBackPressedListener {

    fun finish(requestActivity: FragmentActivity, activity: Activity) {
        requestActivity.onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }

        })
    }

    fun back(requestActivity: FragmentActivity, navController: NavController) {
        requestActivity.onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.popBackStack()
            }
        })
    }
}