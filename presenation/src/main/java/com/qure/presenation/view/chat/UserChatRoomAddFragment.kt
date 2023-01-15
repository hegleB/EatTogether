package com.qure.presenation.view.chat

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.adapter.ChatUserAdapter
import com.qure.presenation.databinding.FragmentUserChatRoomAddBinding
import com.qure.presenation.viewmodel.MessageViewModel
import com.qure.presenation.viewmodel.PeopleViewModel


class UserChatRoomAddFragment :
    BaseFragment<FragmentUserChatRoomAddBinding>(R.layout.fragment_user_chat_room_add) {

    private val args: UserChatRoomAddFragmentArgs by navArgs()
    private val adapter: ChatUserAdapter by lazy {
        ChatUserAdapter()
    }
    private val messageViewModel: MessageViewModel by activityViewModels()
    private val peopleViewModel: PeopleViewModel by activityViewModels()

    override fun init() {
        initViewModel()
        initAdapter()
        observeViewModel()
    }

    private fun initViewModel() {
        binding.viewmodel = messageViewModel
        binding.peopleViewModel = peopleViewModel
        messageViewModel.getUserInfo()
        messageViewModel.getChatRoom(args.chatroom)
    }

    private fun initAdapter() {
        binding.recyclerViewFragmentUserChatRoomAdd.adapter = adapter
        adapter.onItemSelectionChangeListener = {
            messageViewModel.setSelectedUsers(it)
        }
    }

    private fun observeViewModel() {
        messageViewModel.user.observe(viewLifecycleOwner) {
            peopleViewModel.getAllUser(it)
        }


        peopleViewModel.userList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        messageViewModel.buttonAddUsers.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            adapter.onItemSelectionChangeListener = { users ->
                messageViewModel.addChatUsers()
            }
            findNavController().popBackStack()
            it.consume()
        }
    }
}