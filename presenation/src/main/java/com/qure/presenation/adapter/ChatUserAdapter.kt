package com.qure.presenation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.qure.domain.model.User
import com.qure.presenation.R
import com.qure.presenation.base.BaseAdapter
import com.qure.presenation.base.BaseViewHolder
import com.qure.presenation.databinding.ItemChatPeopleBinding
import com.qure.presenation.databinding.ItemChatUserBinding

class ChatUserAdapter :
    BaseAdapter<User>(itemCallback) {

    var onItemSelectionChangeListener: ((MutableList<User>) -> Unit)? = null
    private val seletedUsers = mutableListOf<User>()

    companion object {
        private val itemCallback = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_post
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BaseViewHolder<out ViewDataBinding, User> {
        return ChatViewHolder(
            ItemChatUserBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    inner class ChatViewHolder(binding: ItemChatUserBinding) :
        BaseViewHolder<ItemChatUserBinding, User>(binding) {

        init {
            binding.apply {
            }
        }

        override fun bind(element: User) {
            super.bind(element)
            binding.user = element
            selectChatRoomAddUser(element)
        }

        private fun selectChatRoomAddUser(element: User) {
            binding.root.setOnClickListener {
                binding.checkBoxItemChatUserPeopleProfile.isChecked =
                    if (binding.checkBoxItemChatUserPeopleProfile.isChecked) false else true
                when (seletedUsers.contains(element)) {
                    true -> removeSelectedUser(seletedUsers, element)
                    else -> addSelectedUser(seletedUsers, element)

                }
                onItemSelectionChangeListener?.let { it(seletedUsers) }
            }
        }

        private fun addSelectedUser(
            seletedUsers: MutableList<User>,
            element: User
        ) {
            seletedUsers.add(element)
        }

        private fun removeSelectedUser(
            seletedUsers: MutableList<User>,
            element: User
        ) {
            seletedUsers.remove(element)
        }
    }
}
