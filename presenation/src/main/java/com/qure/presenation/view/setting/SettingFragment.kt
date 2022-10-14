package com.qure.presenation.view.setting

import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.databinding.FragmentProfileSettingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentProfileSettingBinding>(R.layout.fragment_setting) {
    override fun init() {
        println("settingFragment")
    }
}