package com.qure.presenation.view.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.quer.presenation.base.BaseActivity
import com.qure.presenation.R
import com.qure.presenation.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>(R.layout.activity_home) {
    override fun init() {
        println("HomeActivity")
    }

}