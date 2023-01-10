package com.qure.presenation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.qure.domain.model.ChatRoom
import com.qure.presenation.base.BaseAdapter
import com.qure.presenation.base.BaseViewHolder
import com.qure.presenation.databinding.ItemChatBinding

class ChatRoomAdapter(val itemClick: (ChatRoom) -> Unit) : BaseAdapter<ChatRoom>(itemCallback) {

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

        }
    }
}