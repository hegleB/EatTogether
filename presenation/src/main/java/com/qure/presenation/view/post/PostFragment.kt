package com.qure.presenation.view.post

import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.databinding.FragmentPostBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostFragment : BaseFragment<FragmentPostBinding>(R.layout.fragment_post) {
    override fun init() {
        println("postFragment")
    }

}