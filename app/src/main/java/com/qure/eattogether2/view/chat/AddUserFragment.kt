package com.qure.eattogether2.view.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.eattogether2.R
import com.qure.eattogether2.adapter.ChatUserAdapter
import com.qure.eattogether2.data.ChatRoom
import com.qure.eattogether2.data.User
import com.qure.eattogether2.databinding.FragmentAddUserBinding
import com.qure.eattogether2.repository.PeopleRepository
import com.qure.eattogether2.viewmodel.ChatViewModel
import com.qure.eattogether2.viewmodel.PeopleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddUserFragment : Fragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var peopleRepository: PeopleRepository

    private var allChatroomUsers = MutableLiveData<MutableList<User>>()

    private val viewModel by viewModels<PeopleViewModel>()

    private val chatviewModel by viewModels<ChatViewModel>()


    private val adapter = ChatUserAdapter()
    private lateinit var binding: FragmentAddUserBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_user, container, false)



        setUserAdapter()
        getAllChatRoomUser()
        moveToAllUserFragment()



        return binding.root
    }




    fun setUserAdapter() {
        binding.addUserRecyclerview.adapter = adapter

    }

    fun moveToAllUserFragment(){

        var roomId = arguments?.getString("roomId")



        binding.addUserButton.setOnClickListener {



            val intent = Intent(requireContext(), ChatAllUserActivity::class.java)
            intent.putExtra("roomId", roomId)
            startActivity(intent)
        }

    }

    fun getAllChatRoomUser() {

        var roomId = arguments?.getString("roomId")

        val userList = mutableListOf<User>()

        firestore.collection("chatrooms").document(roomId!!).get().addOnSuccessListener { snapshot ->
            val chatroom = snapshot.toObject(ChatRoom::class.java)

            firestore.collection("users").get().addOnSuccessListener { user ->
                for(i in chatroom!!.users){


                    val users = user.toObjects(User::class.java)

                    for(j in users){
                        if(j.uid.equals(i)){
                            userList.add(j)
                        }
                    }
                }

                allChatroomUsers.postValue(userList)

                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.getUsers(allChatroomUsers).collectLatest {
                        adapter.submitData(it)
                    }
                }
            }
        }
    }


}