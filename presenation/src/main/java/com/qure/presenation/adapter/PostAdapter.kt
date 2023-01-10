package com.qure.presenation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.qure.domain.model.PostModel.Post
import com.qure.presenation.R
import com.qure.presenation.base.BaseAdapter
import com.qure.presenation.base.BaseViewHolder
import com.qure.presenation.databinding.ItemPostBinding

class PostAdapter(val itemClick: (Post) -> Unit) : BaseAdapter<Post>(itemCallback) {

    companion object {
        private val itemCallback = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<out ViewDataBinding, Post> {
        return PostHolder(
            ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_post
    }

    inner class PostHolder(binding: ItemPostBinding) :
        BaseViewHolder<ItemPostBinding, Post>(binding) {

        init {
            binding.apply {
                itemView.setOnClickListener {
                    post?.run {
                        itemClick(this)
                    }
                }
            }
        }

        override fun bind(element: Post) {
            super.bind(element)
            binding.post = element

            val images = element.postImages
            binding.linearLayoutItemPostImage1.clipToOutline = true
            if (images.size == 1) {

                binding.linearLayoutItemPostImage1.weightSum = 1F
                Glide.with(itemView.context)
                    .load(images.get(0))
                    .into(binding.imageViewItemPostImage1)

            } else if (images.size == 2) {
                binding.linearLayoutItemPostImage2.weightSum = 1F

                Glide.with(itemView.context).apply {
                    load(images.get(0)).into(binding.imageViewItemPostImage1)
                    load(images.get(1)).into(binding.imageViewItemPostImage2)
                }
            } else if (images.size == 3) {
                Glide.with(itemView.context).apply {
                    load(images.get(0)).into(binding.imageViewItemPostImage1)
                    load(images.get(1)).into(binding.imageViewItemPostImage2)
                    load(images.get(2)).into(binding.imageViewItemPostImage3)
                }

            } else {
                binding.linearLayoutItemPostImages.visibility = View.GONE
            }
        }
    }
}