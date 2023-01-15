package com.qure.presenation.view.chat

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.adapter.ChatRoomAdapter
import com.qure.presenation.databinding.FragmentChatBinding
import com.qure.presenation.utils.BottomNavigationEvent
import com.qure.presenation.utils.OnBackPressedListener
import com.qure.presenation.viewmodel.AuthViewModel
import com.qure.presenation.viewmodel.ChatViewModel
import com.qure.presenation.viewmodel.MessageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(R.layout.fragment_chat) {

    private val chatViewModel: ChatViewModel by activityViewModels()
    private val adapter: ChatRoomAdapter by lazy {
        ChatRoomAdapter(chatViewModel.curruntUid , {
            val direction = ChatFragmentDirections.actionChatFragmentToMessageFragment(it)
            findNavController().navigate(direction)
        })
    }

    override fun init() {
        OnBackPressedListener().finish(requireActivity(), requireActivity())
        BottomNavigationEvent().showBottomNavigation(activity!!)
        initAdapter()
        observeViewModel()
        initViewModel()
    }

    private fun initViewModel() {
        chatViewModel.getAllChatRoom()
    }

    private fun initAdapter() {
        binding.recyclerViewFragmentChat.adapter = adapter
    }

    private fun observeViewModel() {
        chatViewModel.chatRooms.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}