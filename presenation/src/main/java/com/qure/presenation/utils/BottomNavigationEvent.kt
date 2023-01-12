package com.qure.presenation.utils

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.qure.presenation.R

class BottomNavigationEvent {

    fun showBottomNavigation(activity: FragmentActivity) {
        val navBar = activity!!.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navBar.visibility = View.VISIBLE
    }

    fun hideBottomNavigation(activity: FragmentActivity) {
        val navBar = activity!!.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navBar.visibility = View.GONE
    }
}