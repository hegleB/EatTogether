package com.qure.eattogether2.adapter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.qure.eattogether2.R
import com.qure.eattogether2.bindingadapter.ImageBindingAdapter
import com.qure.eattogether2.data.ChatComment
import com.qure.eattogether2.data.ChatRoom
import com.qure.eattogether2.data.User
import com.qure.eattogether2.databinding.ItemChatBinding
import com.qure.eattogether2.view.chat.ChatFragmentDirections

class ChatRoomAdatper(val uid: String, val firestore: FirebaseFirestore) :
    PagingDataAdapter<ChatRoom, ChatRoomAdatper.ViewHolder>(ChatDiffUtil) {


    override fun onBindViewHolder(holder: ChatRoomAdatper.ViewHolder, position: Int) {
        val chatRoom = getItem(position)

        holder.bind(chatRoom!!)
    }

    inner class ViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatroom: ChatRoom) {

            binding.chat = chatroom
            var users = ""
            for (i in chatroom.photo.keys) {
                if (!uid.equals(i)) {
                    ImageBindingAdapter.userImage(binding.chatImageview, chatroom.photo.get(i))
                    firestore.collection("users").document(i).get()
                        .addOnSuccessListener { snapshot ->
                            val user = snapshot.toObject(User::class.java)
                            users+=user!!.usernm+", "

                            binding.chatTitle.setText(users.substring(0,users.length-2))


                            binding.root.setOnClickListener { view ->

                                val directions =
                                    ChatFragmentDirections.actionChatContainerFragmentToMessageFragment2(
                                        chatroom, user.uid, false
                                    )

                                view.findNavController().navigate(directions)


                            }

                        }

                }
            }

            if (chatroom.lastmsg.startsWith("https://firebasestorage")){
                binding.chatLastmsg.setText("사진을 보냈습니다.")

            } else {
                binding.chatLastmsg.setText(chatroom.lastmsg)
            }

            var cnt = chatroom.unreadCount.get(uid)

            if (cnt == 0) {
                binding.chatMsgCount.visibility = View.INVISIBLE
            } else {
                binding.chatMsgCount.visibility = View.VISIBLE
                binding.chatMsgCount.setText(cnt.toString())

            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_chat
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemChatBinding>(
            layoutInflater,
            viewType,
            parent,
            false
        )

        return ViewHolder(binding)
    }


    companion object ChatDiffUtil : DiffUtil.ItemCallback<ChatRoom>() {
        override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem.roomId == newItem.roomId
        }

    }
}

