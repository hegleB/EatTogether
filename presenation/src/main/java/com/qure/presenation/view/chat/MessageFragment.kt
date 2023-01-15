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
import com.qure.presenation.viewmodel.PeopleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageFragment : BaseFragment<FragmentMessageBinding>(R.layout.fragment_message) {

    private val messageViewModel: MessageViewModel by activityViewModels()
    private val peopleViewModel: PeopleViewModel by activityViewModels()
    private val args by navArgs<MessageFragmentArgs>()
    private val adapter: ChatAdapter by lazy {
        ChatAdapter(args.chatroom.userCount, peopleViewModel.currentUid)
    }

    override fun init() {
        BottomNavigationEvent().hideBottomNavigation(activity!!)
        OnBackPressedListener().back(requireActivity(), findNavController())
        initViewModel()
        observeViewModel()
        initRecyclerView()
    }

    private fun initViewModel() {
        binding.viewmodel = messageViewModel
        messageViewModel.getChatRoomId(args.chatRoomId)
        messageViewModel.getChatRoom(args.chatroom)
        messageViewModel.getMessage(args.chatRoomId)
        messageViewModel.getUserInfo()
        messageViewModel.readMessage()
    }

    private fun observeViewModel() {
        messageViewModel.buttonSendMessage.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            KeyboardEvent(requireContext()).hideKeyboard()
            binding.recyclerViewFragmentMessage.scrollToPosition(adapter.itemCount - 1)
            it.consume()
        }

        messageViewModel.messages.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun initRecyclerView() {
        binding.recyclerViewFragmentMessage.adapter = adapter
    }
}