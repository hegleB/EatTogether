package com.qure.eattogether2.adapter

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.qure.eattogether2.R
import com.qure.eattogether2.bindingadapter.ImageBindingAdapter
import com.qure.eattogether2.bindingadapter.TextBindingAdapter
import com.qure.eattogether2.data.ChatComment
import com.qure.eattogether2.databinding.ItemChatLeftBinding
import com.qure.eattogether2.databinding.ItemChatRightBinding
import com.qure.eattogether2.view.chat.*
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.android.synthetic.main.item_chat_left.view.*
import kotlinx.android.synthetic.main.item_chat_right.view.*
import kotlinx.android.synthetic.main.item_message.view.*
import kotlinx.android.synthetic.main.item_message.view.divider
import kotlinx.android.synthetic.main.item_message.view.divider_date
import kotlinx.android.synthetic.main.item_message.view.msg_item
import kotlinx.android.synthetic.main.item_message.view.timestamp
import kotlinx.android.synthetic.main.item_message.view.img_item
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class ChatAdapter(
    val context: Context, val myUid: String, val messageList: MutableList<ChatComment>,
    val userCount: Int
) :
    ListAdapter<ChatComment, RecyclerView.ViewHolder>(ChatCommentDiffUtil) {

    companion object ChatCommentDiffUtil : DiffUtil.ItemCallback<ChatComment>() {


        override fun areItemsTheSame(oldItem: ChatComment, newItem: ChatComment): Boolean {
            return oldItem.roomId == newItem.roomId
        }

        override fun areContentsTheSame(oldItem: ChatComment, newItem: ChatComment): Boolean {
            return oldItem == newItem
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType.equals(R.layout.item_chat_left)) {

            val view =
                ItemChatLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false)

            return MsgViewHolder(view)


        } else {
            val view =
                ItemChatRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)

            return MsgViewHolder(view)

        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItem(position: Int): ChatComment {
        return messageList.get(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = getItem(position)

        (holder as MsgViewHolder<*>).bind(msg, position, userCount, context)
    }

    override fun getItemViewType(position: Int): Int {
        val chat = getItem(position)

        if (myUid.equals(chat.uid)) {
            return R.layout.item_chat_right
        } else {
            return R.layout.item_chat_left
        }

    }


    class MsgViewHolder<T : ViewDataBinding> constructor(val binding: T) :
        RecyclerView.ViewHolder(binding.root) {

        private var beforeDay: String = ""

        fun bind(chat: ChatComment, position: Int, userCount: Int, context: Context) {
            val currentTime: Long = Date().time
            var cnt = userCount - chat.readUsers.size




            TextBindingAdapter.chatTimeText(itemView.timestamp, chat.timestamp)

            if (itemViewType.equals(R.layout.item_chat_left)) {
                ImageBindingAdapter.userImage(itemView.left_user_photo, chat.userImage)
                itemView.left_msg_name.setText(chat.usernm)
                if (cnt > 0) {
                    itemView.left_read_counter.visibility = View.VISIBLE
                    itemView.left_read_counter.setText(cnt.toString())
                } else {
                    itemView.left_read_counter.visibility = View.INVISIBLE
                }
            } else {
                if (cnt > 0) {
                    itemView.right_read_counter.visibility = View.VISIBLE
                    itemView.right_read_counter.setText(cnt.toString())
                } else {
                    itemView.right_read_counter.visibility = View.INVISIBLE
                }

            }

            if (!chat.message.equals("") && chat.messagetype == "1") {
                itemView.img_item.visibility = View.GONE
                itemView.msg_item.visibility = View.VISIBLE
                itemView.msg_item.setText(chat.message)

            } else if (!chat.message.equals("") && chat.messagetype == "2") {
                itemView.img_item.visibility = View.VISIBLE
                itemView.msg_item.visibility = View.GONE
                itemView.msg_item.clipToOutline = true
                Glide.with(itemView.img_item.context).load(chat.message)
                    .error(R.drawable.ic_close_circle).placeholder(R.drawable.anim_loading)
                    .into(itemView.img_item)

                itemView.img_item.setOnClickListener { view ->
                    val directions = ImageViewerFragmentDirections.actionToImageViewrFragment(chat.message)

                    view.findNavController().navigate(directions)

                }


            }

            val currentDate = SimpleDateFormat("yyyy-MM-dd")
            val day = currentDate.format(currentTime).toString()

            itemView.divider.visibility = View.INVISIBLE
            itemView.divider.layoutParams.height = 0


            if (position == 0) {
                itemView.divider_date.setText(day)
                itemView.divider.visibility = View.VISIBLE
                itemView.divider.layoutParams.height = 60
            }

            if (!day.equals(beforeDay) && beforeDay != "") {
                itemView.divider_date.setText(beforeDay)
                itemView.divider.visibility = View.VISIBLE
                itemView.divider.layoutParams.height = 60
            }

            beforeDay = day

        }
    }
}