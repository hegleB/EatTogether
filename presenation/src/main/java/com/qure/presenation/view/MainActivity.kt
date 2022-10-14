package com.qure.presenation.view

import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.quer.presenation.base.BaseActivity
import com.qure.presenation.R
import com.qure.presenation.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    override fun init() {
        val navController : NavController = Navigation.findNavController(this, R.id.nav_host_fragment)
        navController.setGraph(R.navigation.nav_graph)
    }





}