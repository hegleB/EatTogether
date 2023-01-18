package com.qure.presenation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.qure.domain.model.PostModel
import com.qure.presenation.base.BaseAdapter
import com.qure.presenation.base.BaseViewHolder
import com.qure.presenation.databinding.ItemPostImageBinding

class PostImagesAdapter :
    BaseAdapter<PostModel.PostImage>(itemCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BaseViewHolder<out ViewDataBinding, PostModel.PostImage> {
        return ViewHolder(
            ItemPostImageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    inner class ViewHolder(binding: ItemPostImageBinding) :
        BaseViewHolder<ItemPostImageBinding, PostModel.PostImage>(binding) {

        override fun bind(element: PostModel.PostImage) {
            super.bind(element)
            binding.image = element
        }
    }

    companion object {
        private val itemCallback = object : DiffUtil.ItemCallback<PostModel.PostImage>() {
            override fun areItemsTheSame(
                oldItem: PostModel.PostImage,
                newItem: PostModel.PostImage,
            ): Boolean {
                return oldItem.postkey == newItem.postkey
            }

            override fun areContentsTheSame(
                oldItem: PostModel.PostImage,
                newItem: PostModel.PostImage,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
