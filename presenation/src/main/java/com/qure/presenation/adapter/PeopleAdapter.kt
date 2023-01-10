package com.qure.presenation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.qure.domain.model.User
import com.qure.presenation.base.BaseAdapter
import com.qure.presenation.base.BaseViewHolder
import com.qure.presenation.databinding.ItemPeopleBinding

class PeopleAdapter(val itemClick: (User) -> Unit) : BaseAdapter<User>(itemCallback) {

    companion object {
        private val itemCallback = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<out ViewDataBinding, User> {
        return PeopleHolder(
            ItemPeopleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    inner class PeopleHolder(binding: ItemPeopleBinding) :
        BaseViewHolder<ItemPeopleBinding, User>(binding) {

        init {
            binding.apply {
                itemView.setOnClickListener {
                    user?.run {
                        itemClick(this@run)
                    }
                }
            }
        }

        override fun bind(element: User) {
            super.bind(element)
            binding.user = element
        }
    }
}