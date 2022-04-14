package com.qure.eattogether2.adapter

import com.qure.eattogether2.databinding.ItemChatPeopleBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.qure.eattogether2.R

import com.qure.eattogether2.data.User

class ChatUserAdapter :
    PagingDataAdapter<User, ChatUserAdapter.ViewHolder>(PeopleDiffUtil) {


    override fun onBindViewHolder(holder: ChatUserAdapter.ViewHolder, position: Int) {
        val people = getItem(position)

        holder.bind(people!!)

    }

    inner class ViewHolder(private val binding: ItemChatPeopleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(people: User) {

            binding.user = people
        }

    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_chat_people
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ItemChatPeopleBinding>(layoutInflater, viewType, parent, false)

        return ViewHolder(binding)
    }


    companion object PeopleDiffUtil : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }
}

