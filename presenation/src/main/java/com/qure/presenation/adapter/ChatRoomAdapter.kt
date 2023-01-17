package com.qure.presenation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.model.ChatRoom
import com.qure.domain.model.User
import com.qure.domain.utils.USERS_COLLECTION_PATH
import com.qure.presenation.adapter.bindingadapter.ImageBindingAdapter
import com.qure.presenation.base.BaseAdapter
import com.qure.presenation.base.BaseViewHolder
import com.qure.presenation.databinding.ItemChatBinding

class ChatRoomAdapter(val uid: String, val itemClick: (ChatRoom) -> Unit) :
    BaseAdapter<ChatRoom>(itemCallback) {

    companion object {
        private val itemCallback = object : DiffUtil.ItemCallback<ChatRoom>() {
            override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<out ViewDataBinding, ChatRoom> {
        return ChatRoomHolder(
            ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    inner class ChatRoomHolder(binding: ItemChatBinding) :
        BaseViewHolder<ItemChatBinding, ChatRoom>(binding) {

        init {
            binding.apply {
                itemView.setOnClickListener {
                    chat?.run {
                        itemClick(this)
                    }
                }
            }
        }

        override fun bind(element: ChatRoom) {
            super.bind(element)
            binding.chat = element
            var users = ""
            for (i in element.photo.keys) {
                if (!uid.equals(i)) {
                    ImageBindingAdapter.userImage(
                        binding.circleImageViewItemChat,
                        element.photo.get(i)
                    )
                    FirebaseFirestore.getInstance().collection(USERS_COLLECTION_PATH).document(i).get()
                        .addOnSuccessListener { snapshot ->
                            val user = snapshot.toObject(User::class.java)
                            users += user!!.usernm + ", "
                            binding.textViewItemChatTitle.setText(
                                users.substring(0, users.length - 2)
                            )
                        }

                }
            }

            if (element.lastmsg.startsWith("https://firebasestorage")) {
                binding.textViewItemChatLastMsg.setText("사진을 보냈습니다.")

            } else {
                binding.textViewItemChatLastMsg.setText(element.lastmsg)
            }

            var cnt = element.unreadCount.get(uid)

            if (cnt == 0) {
                binding.textViewItemChatMsgCount.visibility = View.INVISIBLE
            } else {
                binding.textViewItemChatMsgCount.visibility = View.VISIBLE
                binding.textViewItemChatMsgCount.setText(cnt.toString())

            }
        }
    }
}