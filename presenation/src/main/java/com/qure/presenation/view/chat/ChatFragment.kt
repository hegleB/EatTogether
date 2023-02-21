package com.qure.presenation.view.chat

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.adapter.ChatRoomAdapter
import com.qure.presenation.databinding.FragmentChatBinding
import com.qure.presenation.utils.BottomNavigationEvent
import com.qure.presenation.utils.OnBackPressedListener
import com.qure.presenation.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(R.layout.fragment_chat) {

    private val chatViewModel: ChatViewModel by activityViewModels()
    private val adapter: ChatRoomAdapter by lazy {
        ChatRoomAdapter(chatViewModel.currentUid.value ?: "", {
            val direction = ChatFragmentDirections.actionChatFragmentToMessageFragment(
                it,
                "",
                false,
            )
            findNavController().navigate(direction)
        })
    }

    override fun init() {
        OnBackPressedListener().finish(requireActivity(), requireActivity())
        BottomNavigationEvent().showBottomNavigation(activity!!)
        initViewModel()
        observeViewModel()
        initAdapter()
    }

    private fun initViewModel() {
        chatViewModel.getCurrentUid(currentUid)
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
