package com.qure.presenation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import com.qure.presenation.base.*
import com.qure.presenation.databinding.ItemCreateImageBinding
import com.qure.presenation.viewmodel.PostViewModel

class PostCreateImageAdapter(val postViewModel: PostViewModel, val viewLifecycleOwner: LifecycleOwner) : BaseAdapter<String>(itemCallback){

    companion object {
        private val itemCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<out ViewDataBinding, String> {
        return ViewHolder(
            ItemCreateImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    inner class ViewHolder(binding: ItemCreateImageBinding)
        : BaseViewHolder<ItemCreateImageBinding, String>(binding) {

        override fun bind(element: String) {
            super.bind(element)
            binding.image = element
            binding.viewmodel = postViewModel

            binding.imageViewItemCreateImageDelete.setOnClickListener {
                postViewModel.deletePostCreateImage(element)
            }

        }
    }
}