package com.qure.eattogether2.view.chat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.eattogether2.R
import com.qure.eattogether2.adapter.ChatAllUserAdapter
import com.qure.eattogether2.data.ChatRoom
import com.qure.eattogether2.data.User
import com.qure.eattogether2.databinding.ActivityChatAllUserBinding
import com.qure.eattogether2.repository.PeopleRepository
import com.qure.eattogether2.viewmodel.PeopleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatAllUserActivity : AppCompatActivity() {

    @Inject
    lateinit var peopleRepository: PeopleRepository

    @Inject
    lateinit var firestore: FirebaseFirestore

    private val viewModel by viewModels<PeopleViewModel>()


    private lateinit var binding: ActivityChatAllUserBinding
    private val adapter = ChatAllUserAdapter()
    private var allUsers = MutableLiveData<MutableList<User>>()

    private var selectedUserList = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_all_user)


        val intent: Intent = intent
        val roomId = intent.getStringExtra("roomId")

        setUserAdapter()
        selectUser()
        getAllUser(roomId!!)

        binding.chatAllUserSubmit.setOnClickListener {

            addChatRoomUser(roomId!!)
            onBackPressed()

        }

        binding.chatAllUserCancel.setOnClickListener{

            onBackPressed()
        }

    }

    fun setUserAdapter() {
        binding.chatAllUserRecyclerview.adapter = adapter

    }

    fun addChatRoomUser(roomId: String) {

        firestore.collection("chatrooms").document(roomId).get()
            .addOnSuccessListener { snapshot ->
                val chatroom = snapshot.toObject(ChatRoom::class.java)
                val chatroomPhoto = chatroom!!.photo
                val unReadCount = chatroom!!.unreadCount
                val userList = chatroom!!.users
                val seletedList = selectedUserList
                firestore.collection("users").get().addOnCompleteListener { task ->


                    if (task.isSuccessful) {
                        val u = task.result.toObjects(User::class.java)

                        for (i in u) {

                            if (seletedList!!.contains(i.uid)) {

                                chatroomPhoto.put(i.uid, i.userphoto)
                                unReadCount.put(i.uid, 0)
                                userList.add(i.uid)
                            }

                        }
                        val newChatRoom = ChatRoom(
                            room = true,
                            roomId,
                            chatroom!!.title,
                            chatroomPhoto,
                            chatroom.lastmsg,
                            chatroom.lastDate,
                            chatroom.userCount + seletedList!!.size,
                            unReadCount,
                            userList
                        )


                        firestore.collection("chatrooms").document(roomId).set(newChatRoom)
                    }
                }
            }


    }

    fun selectUser() {
        var userlist = mutableListOf<String>()
        adapter.setItemClickListener(object : ChatAllUserAdapter.ItemClickListener {
            override fun onClick(view: View, user: User) {

                if (userlist.contains(user.uid)) {
                    userlist.remove(user.uid)
                } else {
                    userlist.add(user.uid)
                }

                selectedUserList=userlist
                System.out.println("리스트: "+ selectedUserList)

            }

        })
    }


    fun getAllUser(roomId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            peopleRepository.getAllUser().collectLatest {
                val userList = mutableListOf<User>()
                for (i in it) {
                    if (!FirebaseAuth.getInstance().currentUser!!.uid.equals(i.uid)) {
                        userList.add(i)
                    }
                }
                firestore.collection("chatrooms").document(roomId).get()
                    .addOnSuccessListener { snapshot ->
                        val users = snapshot.toObject(ChatRoom::class.java)
                        val uList = users!!.users.toMutableList()

                        firestore.collection("users").get().addOnSuccessListener { user ->
                            val u = user.toObjects(User::class.java)
                            for (i in u) {
                                if (uList.contains(i.uid)) {

                                    userList.remove(i)
                                }

                            }
                            allUsers.postValue(userList.toMutableList())
                            CoroutineScope(Dispatchers.Main).launch {
                                viewModel.getUsers(allUsers).collectLatest {
                                    adapter.submitData(it)
                                }
                            }
                        }

                    }
            }
        }
    }
}