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
        viewType: Int,
    ): BaseViewHolder<out ViewDataBinding, ChatRoom> {
        return ChatRoomHolder(
            ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    inner class ChatRoomHolder(binding: ItemChatBinding) :
        BaseViewHolder<ItemChatBinding, ChatRoom>(binding) {

        private val LAST_IMAGE_MESSAGE = "사진을 보냈습니다."
        private val LAST_MESSAGE_START_URL = "https://firebasestorage"
        private val MIN_MESSAGE_COUNT = "0"
        private var userNames = "나, "

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
            setChatRoomImageAndName(element)
            setLastMessageText(element)
            setMessageCount(element)
        }

        private fun setChatRoomImageAndName(element: ChatRoom) {
            for (key in element.photo.keys) {
                if (!uid.equals(key)) {
                    setChatRoomUserImage(element, key)
                    setChatRoomUserName(key)
                }
            }
        }

        private fun setChatRoomUserImage(element: ChatRoom, key: String) {
            ImageBindingAdapter.userImage(
                binding.circleImageViewItemChat,
                element.photo.get(key),
            )
        }

        private fun setChatRoomUserName(key: String) {
            FirebaseFirestore.getInstance().collection(USERS_COLLECTION_PATH)
                .document(key)
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        val user = snapshot.toObject(User::class.java)
                        userNames += "${user?.usernm ?: ""}, "
                        binding.textViewItemChatTitle.text = userNames.substring(0, userNames.length - 2)
                    }
                }
        }

        private fun setMessageCount(element: ChatRoom) {
            var cnt = element.unreadCount.get(uid) ?: 0
            binding.apply {
                textViewItemChatMsgCount.visibility = if (cnt == 0) View.INVISIBLE else View.VISIBLE
                textViewItemChatMsgCount.text = if (cnt != 0) cnt.toString() else MIN_MESSAGE_COUNT
            }
        }

        private fun setLastMessageText(element: ChatRoom) {
            binding.textViewItemChatLastMsg.text =
                if (isImageMessage(element)) LAST_IMAGE_MESSAGE else element.lastmsg
        }

        private fun isImageMessage(element: ChatRoom): Boolean =
            element.lastmsg.startsWith(LAST_MESSAGE_START_URL)
    }
}
