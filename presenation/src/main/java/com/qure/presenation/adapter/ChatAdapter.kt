package com.qure.presenation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.qure.domain.model.ChatMessage
import com.qure.presenation.R
import com.qure.presenation.adapter.viewholder.MessageViewHolder
import com.qure.presenation.databinding.ItemChatLeftBinding
import com.qure.presenation.databinding.ItemChatRightBinding
import com.qure.presenation.viewmodel.MessageViewModel

class ChatAdapter(val userCount: Int, val uid: String) :
    ListAdapter<ChatMessage, MessageViewHolder>(itemCallback) {

    companion object {
        private val itemCallback = object : DiffUtil.ItemCallback<ChatMessage>() {
            override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
                return oldItem.timestamp == newItem.timestamp
            }

            override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val chat = getItem(position)
        if (uid.equals(chat.uid)) {
            return R.layout.item_chat_right
        }
        return R.layout.item_chat_left
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when (viewType) {
            R.layout.item_chat_left -> MessageViewHolder.ChatLeftViewHoler(
                ItemChatLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                userCount
            )
            else -> MessageViewHolder.ChatRightViewHolder(
                ItemChatRightBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                userCount
            )
        }

    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val msg = getItem(position)
        holder.bind(msg)
    }
}