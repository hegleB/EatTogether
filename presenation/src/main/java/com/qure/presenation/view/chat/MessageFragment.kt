package com.qure.presenation.view.chat

import android.net.Uri
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
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
import gun0912.tedbottompicker.TedBottomPicker
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
        messageViewModel.getChatRoom(args.chatroom)
        messageViewModel.getMessage(args.chatroom.roomId)
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

            override fun onPermissionDenied(deniedPermissions: java.util.ArrayList<String>?) {
                Snackbar.make(
                    binding.constrainLayoutFragmentMessageMessage, "Permission Denied\n" +
                            deniedPermissions.toString(), Snackbar.LENGTH_LONG
                ).show()
            }

        }
        TedPermission.with(requireContext())
            .setPermissionListener(permissionListener)
            .setRationaleMessage("사진을 추가하기 위해서는 권한 설정이 필요합니다.")
            .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다..")
            .setPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check()
    }

    private fun openImagePicker() {
        TedBottomPicker.with(requireActivity())
            .setPeekHeight(1600)
            .showGalleryTile(false)
            .setPreviewMaxCount(1000)
            .setSelectMaxCount(1)
            .setSelectMaxCountErrorText("1개만 선택이 가능합니다.")
            .showTitle(false)
            .setTitleBackgroundResId(R.color.light_red)
            .setGalleryTileBackgroundResId(R.color.white)
            .setCompleteButtonText("전송")
            .setEmptySelectionText("사진 선택")
            .showMultiImage(object : TedBottomSheetDialogFragment.OnMultiImageSelectedListener {
                override fun onImagesSelected(uriList: MutableList<Uri>) {
                    if (uriList.size > 0) {
                        val ref = firebaseStorage.getReference()
                            .child("chat_image/" + args.chatroom.roomId + "/" + Date().time.toString() + ".jpg")
                        val uploadTask = ref.putFile(uriList.get(0))
                        uploadTask.addOnSuccessListener {
                            ref.downloadUrl.addOnSuccessListener { uri ->
                                messageViewModel.sendMessageImage(uri.toString())
                            }
                        }
                    }
                }
            })
    }

    private fun setMessageToolbar() {
        binding.apply {
            toolBarFragmentMessage.inflateMenu(R.menu.chat_navigation)
            toolBarFragmentMessage.setNavigationIcon(R.drawable.ic_back)
            toolBarFragmentMessage.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            binding.toolBarFragmentMessage.setOnMenuItemClickListener(object :
                Toolbar.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    when (item!!.itemId) {
                        R.id.menu_add_user -> {
                            findNavController().navigate(MessageFragmentDirections.actionMessageFragmentToUserChatRoomAddFragment(
                                args.chatroom
                            ))
                            return true
                        }
                        else -> {
                            return true
                        }
                    }
                }

            })
        }
    }
}