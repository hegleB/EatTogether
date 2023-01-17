package com.qure.presenation.view.people

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.model.ChatRoom
import com.qure.presenation.R
import com.qure.presenation.base.BaseBottomSheetFragment
import com.qure.presenation.databinding.DialogProfileBinding
import com.qure.presenation.viewmodel.ChatViewModel
import com.qure.presenation.viewmodel.PeopleViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileDialogFragment :
    BaseBottomSheetFragment<DialogProfileBinding>(R.layout.dialog_profile) {

    private val peopleViewModel: PeopleViewModel by activityViewModels()
    private val chatViewModel: ChatViewModel by activityViewModels()
    private val args: ProfileDialogFragmentArgs by navArgs()
    @Inject
    lateinit var firestore: FirebaseFirestore

    override fun init() {
        initViewModel()
        observeViewModel()
    }

    private fun initViewModel() {
        binding.viewmodel = peopleViewModel
        chatViewModel.getUserInfo(peopleViewModel.currentUid)
        chatViewModel.getUserInfo(args.peopleOtherPersonUid)
        chatViewModel.getAllChatRoom()
    }

    private fun observeViewModel() {

        peopleViewModel.otherProfileInfo.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            moveToProfile(args.peopleOtherPersonUid)
            peopleViewModel.setClosedProfileState()
            it.consume()
        }

        peopleViewModel.chatRoom.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            createOneToOneChatRoom()
            it.consume()
        }
    }

    private fun moveToProfile(uid: String) {
        val direction =
            ProfileDialogFragmentDirections.actionProfileDialogFragmentToProfileDetailFragment(
                uid
            )
        findNavController().navigate(direction)
    }

    private fun moveToChatRoom(chatRoom: ChatRoom) {
        val direction = ProfileDialogFragmentDirections.actionPeopleFragmentToMessageFragment(
            chatRoom, args.peopleOtherPersonUid,false
        )
        findNavController().navigate(direction)
    }

    private fun createOneToOneChatRoom() {
        try {
            val chatroom = chatViewModel.findChatRoom(args.peopleOtherPersonUid)
            moveToChatRoom(chatroom)
        } catch (e: IllegalArgumentException) {
            val chatRoom = chatViewModel.setChatRoom(args.peopleOtherPersonUid)
            moveToChatRoom(chatRoom)
        }

    }
}