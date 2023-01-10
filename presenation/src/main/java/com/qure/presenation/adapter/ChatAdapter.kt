package com.qure.presenation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.qure.domain.model.ChatComment
import com.qure.presenation.R
import com.qure.presenation.adapter.viewholder.MessageViewHolder
import com.qure.presenation.databinding.ItemChatLeftBinding
import com.qure.presenation.databinding.ItemChatRightBinding
import com.qure.presenation.viewmodel.MessageViewModel

class ChatAdapter(viewModel: MessageViewModel) :
    ListAdapter<ChatComment, MessageViewHolder>(itemCallback) {
    private val uid = viewModel.user.value?.uid ?: ""

    companion object {
        private val itemCallback = object : DiffUtil.ItemCallback<ChatComment>() {
            override fun areItemsTheSame(oldItem: ChatComment, newItem: ChatComment): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: ChatComment, newItem: ChatComment): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val chat = getItem(position)

        if (uid.equals(chat.uid)) {
            return R.layout.item_chat_right
        } else {
            return R.layout.item_chat_left
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when (viewType) {
            R.layout.item_chat_left -> MessageViewHolder.ChatLeftViewHoler(
                ItemChatLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> MessageViewHolder.ChatRightViewHolder(
                ItemChatRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val msg = getItem(position)
        holder.bind(msg)
    }
}