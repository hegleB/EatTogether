package com.qure.presenation.adapter.viewholder

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qure.domain.model.ChatComment
import com.qure.presenation.adapter.bindingadapter.TextBindingAdapter
import com.qure.presenation.databinding.ItemChatLeftBinding
import com.qure.presenation.databinding.ItemChatRightBinding
import java.text.SimpleDateFormat
import java.util.*

sealed class MessageViewHolder(
    binding: ViewDataBinding
) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(item: ChatComment)

    class ChatLeftViewHoler(
        private val binding: ItemChatLeftBinding
    ) : MessageViewHolder(binding) {
        override fun bind(item: ChatComment) {
            binding.chatLeft = item
        }
    }

    class ChatRightViewHolder(
        private val binding: ItemChatRightBinding
    ) : MessageViewHolder(binding) {
        override fun bind(item: ChatComment) {
            binding.chatRight = item
        }
    }
}