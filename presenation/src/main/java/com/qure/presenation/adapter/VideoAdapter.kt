package com.qure.presenation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.qure.domain.model.Items
import com.qure.presenation.base.BaseViewHolder
import com.qure.presenation.databinding.ItemVideoBinding

class VideoAdapter :
    PagingDataAdapter<Items, BaseViewHolder<ItemVideoBinding, Items>>(VideoDiffiUtil) {

    companion object {
        private val VideoDiffiUtil = object : DiffUtil.ItemCallback<Items>() {
            override fun areItemsTheSame(oldItem: Items, newItem: Items): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Items, newItem: Items): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemVideoBinding, Items>, position: Int) {
        getItem(position).let { holder.bind(it!!) }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemVideoBinding, Items> {
        return VideoViewHolder(
            ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    inner class VideoViewHolder(binding: ItemVideoBinding) :
        BaseViewHolder<ItemVideoBinding, Items>(binding) {
        init {
            binding.apply {

            }
        }

        override fun bind(element: Items) {
            super.bind(element)
            binding.items = element
        }
    }
}