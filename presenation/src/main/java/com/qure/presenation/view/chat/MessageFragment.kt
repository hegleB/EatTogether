package com.qure.presenation.view.chat

import android.net.Uri
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.quer.presenation.base.BaseFragment
import com.qure.presenation.utils.BottomImagePicker
import com.qure.presenation.R
import com.qure.presenation.adapter.ChatAdapter
import com.qure.presenation.databinding.FragmentMessageBinding
import com.qure.presenation.utils.BottomNavigationEvent
import com.qure.presenation.utils.KeyboardEvent
import com.qure.presenation.utils.OnBackPressedListener
import com.qure.presenation.viewmodel.MessageViewModel
import com.qure.presenation.viewmodel.PeopleViewModel
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedbottompicker.TedBottomSheetDialogFragment
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MessageFragment : BaseFragment<FragmentMessageBinding>(R.layout.fragment_message) {

    private val messageViewModel: MessageViewModel by activityViewModels()
    private val peopleViewModel: PeopleViewModel by activityViewModels()
    private val args by navArgs<MessageFragmentArgs>()
    private val adapter: ChatAdapter by lazy {
        ChatAdapter(args.chatroom.userCount, peopleViewModel.currentUid)
    }
    private val bottomImagePicker by lazy {
        BottomImagePicker(requireContext(), requireActivity())
    }

    @Inject
    lateinit var firebaseStorage: FirebaseStorage

    override fun init() {
        BottomNavigationEvent().hideBottomNavigation(activity!!)
        OnBackPressedListener().back(requireActivity(), findNavController())
        initViewModel()
        observeViewModel()
        initRecyclerView()
        setMessageToolbar()
    }

    private fun initViewModel() {
        binding.viewmodel = messageViewModel
        messageViewModel.apply {
            getChatRoom(args.chatroom)
            getMessage(args.chatroom.roomId)
            getUserInfo()
            readMessage()
        }
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

        messageViewModel.buttonImageSelection.observe(viewLifecycleOwner) {
            if (it.consumed) requestPermission()
            requestPermission()
            it.consume()
        }
    }

    private fun initRecyclerView() {
        binding.recyclerViewFragmentMessage.adapter = adapter
    }

    private fun requestPermission() {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                openImagePicker()
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                showPermissionSnackBar(deniedPermissions)
            }
        }
        bottomImagePicker.setPermission(permissionListener)
    }

    private fun showPermissionSnackBar(deniedPermissions: ArrayList<String>?) {
        bottomImagePicker.showSnackBarMessage(requireView(), deniedPermissions ?: arrayListOf())
    }

    private fun openImagePicker() {
        bottomImagePicker.openImagePicker("3개만 선택이 가능합니다.", "선택")
            .showMultiImage { uriList ->
                if (uriList.size > 0) {
                    uploadImageMessage(uriList)
                }
            }
    }

    private fun uploadImageMessage(uriList: MutableList<Uri>) {
        val ref = firebaseStorage.getReference()
            .child("chat_image/" + args.chatroom.roomId + "/" + Date().time.toString() + ".jpg")
        val uploadTask = ref.putFile(uriList.get(0))
        uploadTask.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->
                messageViewModel.sendMessageImage(uri.toString())
            }
        }
    }

    private fun setMessageToolbar() {
        binding.toolBarFragmentMessage.apply {
            inflateMenu(R.menu.chat_navigation)
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            clickAddUserMenu()
        }
    }

    private fun clickAddUserMenu() {
        binding.toolBarFragmentMessage.setOnMenuItemClickListener(object :
            Toolbar.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when (item!!.itemId) {
                    R.id.menu_add_user -> moveToAddChatRoomUser()
                }
                return true
            }
        })
    }

    private fun moveToAddChatRoomUser() {
        findNavController().navigate(
            MessageFragmentDirections.actionMessageFragmentToUserChatRoomAddFragment(args.chatroom)
        )
    }
}