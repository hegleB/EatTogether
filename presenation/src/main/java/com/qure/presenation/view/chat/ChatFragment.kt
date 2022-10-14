package com.qure.presenation.view.chat

import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(R.layout.fragment_chat) {
    override fun init() {
        println("chatFragment")
    }

}