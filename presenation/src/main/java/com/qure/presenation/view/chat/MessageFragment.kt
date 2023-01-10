package com.qure.presenation.view.chat

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.adapter.ChatAdapter
import com.qure.presenation.databinding.FragmentMessageBinding
import com.qure.presenation.utils.BottomNavigationEvent
import com.qure.presenation.utils.KeyboardEvent
import com.qure.presenation.utils.OnBackPressedListener
import com.qure.presenation.viewmodel.MessageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageFragment : BaseFragment<FragmentMessageBinding>(R.layout.fragment_message) {

    override fun init() {
        BottomNavigationEvent().hideBottomNavigation(activity!!)
        OnBackPressedListener().back(requireActivity(), findNavController())
    }

}