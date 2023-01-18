package com.qure.presenation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.qure.domain.model.PostModel
import com.qure.presenation.R
import com.qure.presenation.base.BaseAdapter
import com.qure.presenation.base.BaseViewHolder
import com.qure.presenation.databinding.ItemProfileViewpagerBinding
import com.qure.presenation.viewmodel.PostViewModel

class ProfilePostAdapter(
    val itemClick: (PostModel.Post) -> Unit,
    val postsViewModel: PostViewModel,
) : BaseAdapter<PostModel.Post>(itemCallback) {

    companion object {
        private val itemCallback = object : DiffUtil.ItemCallback<PostModel.Post>() {
            override fun areItemsTheSame(
                oldItem: PostModel.Post,
                newItem: PostModel.Post,
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(
                oldItem: PostModel.Post,
                newItem: PostModel.Post,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BaseViewHolder<out ViewDataBinding, PostModel.Post> {
        return PostHolder(
            ItemProfileViewpagerBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_profile_viewpager
    }

    inner class PostHolder(binding: ItemProfileViewpagerBinding) :
        BaseViewHolder<ItemProfileViewpagerBinding, PostModel.Post>(binding) {

        init {
            binding.apply {
                itemView.setOnClickListener {
                    profilePost?.run {
                        itemClick(this)
                    }
                }
            }
        }

        override fun bind(element: PostModel.Post) {
            super.bind(element)
            binding.profilePost = element
        }
    }
}
