package com.qure.eattogether2.view.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.eattogether2.R
import com.qure.eattogether2.adapter.ChatRoomAdatper
import com.qure.eattogether2.data.ChatRoom
import com.qure.eattogether2.databinding.FragmentChatBinding
import com.qure.eattogether2.repository.ChatRepository
import com.qure.eattogether2.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ChatFragment : Fragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    @Inject
    lateinit var chatRepository: ChatRepository

    private val viewModel by viewModels<ChatViewModel>()
    private lateinit var navBar: BottomNavigationView
    private lateinit var binding : FragmentChatBinding
    private var allChatRooms = MutableLiveData<List<ChatRoom>>()
    private lateinit var callback: OnBackPressedCallback



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)

        getAllChatRoom()
        onBackpressed()


        return binding.root
    }


    fun  getAllChatRoom() {
        val adapter = ChatRoomAdatper(firebaseAuth.currentUser!!.uid, firestore)

        val currentUid = firebaseAuth.currentUser!!.uid

        val roomList = chatRepository.getAllChatRoom(currentUid)

        CoroutineScope(Dispatchers.Main).launch {
            roomList.collectLatest {

                allChatRooms.postValue(it)


                viewModel.getChatRooms(allChatRooms).collectLatest {
                    adapter.submitData(it)
                }
            }
        }
        binding.chatRecyclerview.adapter = adapter
        binding.chatRecyclerview.apply { itemAnimator = null }



    }

    fun onBackpressed(){
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
               requireActivity().finish()

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
    }


    override fun onDestroy() {
        super.onDestroy()
        navBar = requireActivity().findViewById(R.id.bottom_navigation)
        navBar.visibility = View.VISIBLE


    }

}