package com.qure.presenation.adapter.viewholder

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qure.domain.model.ChatMessage
import com.qure.presenation.R
import com.qure.presenation.adapter.bindingadapter.ImageBindingAdapter
import com.qure.presenation.databinding.ItemChatLeftBinding
import com.qure.presenation.databinding.ItemChatRightBinding
import java.text.SimpleDateFormat
import java.util.*


sealed class MessageViewHolder(
    binding: ViewDataBinding
) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(item: ChatMessage)

    class ChatLeftViewHoler(
        private val binding: ItemChatLeftBinding,
        private val userCount: Int
    ) : MessageViewHolder(binding) {
        private var beforeDay = ""

        override fun bind(item: ChatMessage) {
            var cnt = userCount - item.readUsers.size
            binding.chatLeft = item
            ImageBindingAdapter.userImage(binding.leftUserPhoto, item.userImage)
            binding.leftMsgName.setText(item.usernm)
            if (cnt > 0) {
                binding.leftReadCounter.visibility = View.VISIBLE
                binding.leftReadCounter.setText(cnt.toString())
            } else {
                binding.leftReadCounter.visibility = View.INVISIBLE
            }

            if (!item.message.equals("") && item.messagetype == "1") {
                binding.msgItem.visibility = View.GONE
                binding.msgItem.visibility = View.VISIBLE
                binding.msgItem.setText(item.message)

            } else if (!item.message.equals("") && item.messagetype == "2") {
                binding.imgItem.visibility = View.VISIBLE
                binding.msgItem.visibility = View.GONE
                binding.msgItem.clipToOutline = true
                Glide.with(binding.imgItem.context).load(item.message)
                    .error(R.drawable.ic_close_circle)
                    .into(binding.imgItem)
            }
            val currentTime: Long = Date().time
            val currentDate = SimpleDateFormat("yyyy-MM-dd")
            val day = currentDate.format(currentTime).toString()

            binding.divider.visibility = View.INVISIBLE
            binding.divider.layoutParams.height = 0

            if (position == 0) {
                binding.dividerDate.setText(day)
                binding.divider.visibility = View.VISIBLE
                binding.divider.layoutParams.height = 60
            }

            if (!day.equals(beforeDay) && beforeDay != "") {
                binding.dividerDate.setText(beforeDay)
                binding.divider.visibility = View.VISIBLE
                binding.divider.layoutParams.height = 60
            }
            beforeDay = day
        }
    }

    class ChatRightViewHolder(
        private val binding: ItemChatRightBinding,
        private val userCount: Int
    ) : MessageViewHolder(binding) {
        private var beforeDay = ""

        override fun bind(item: ChatMessage) {
            binding.chatRight = item
            println("RightUserCount : " + userCount)
            println("RightReadUsers : " + item.readUsers.size)
            var cnt = userCount - item.readUsers.size
            if (cnt > 0) {
                binding.rightReadCounter.visibility = View.VISIBLE
                binding.rightReadCounter.setText(cnt.toString())
            } else {
                binding.rightReadCounter.visibility = View.INVISIBLE
            }

            if (!item.message.equals("") && item.messagetype == "1") {
                binding.msgItem.visibility = View.GONE
                binding.msgItem.visibility = View.VISIBLE
                binding.msgItem.setText(item.message)

            } else if (!item.message.equals("") && item.messagetype == "2") {
                binding.imgItem.visibility = View.VISIBLE
                binding.msgItem.visibility = View.GONE
                binding.msgItem.clipToOutline = true
                Glide.with(binding.imgItem.context).load(item.message)
                    .error(R.drawable.ic_close_circle)
                    .into(binding.imgItem)
            }
            val currentTime: Long = Date().time
            val currentDate = SimpleDateFormat("yyyy-MM-dd")
            val day = currentDate.format(currentTime).toString()

            binding.divider.visibility = View.INVISIBLE
            binding.divider.layoutParams.height = 0

            if (position == 0) {
                binding.dividerDate.setText(day)
                binding.divider.visibility = View.VISIBLE
                binding.divider.layoutParams.height = 60
            }

            if (!day.equals(beforeDay) && beforeDay != "") {
                binding.dividerDate.setText(beforeDay)
                binding.divider.visibility = View.VISIBLE
                binding.divider.layoutParams.height = 60
            }
            beforeDay = day
        }
    }
}