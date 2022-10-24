package com.qure.presenation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.qure.domain.model.Comments
import com.qure.domain.usecase.comment.CheckReCommentUseCase
import com.qure.domain.utils.Resource
import com.qure.presenation.databinding.ItemRecommentsBinding
import com.qure.presenation.viewmodel.PostViewModel
import kotlinx.coroutines.launch

class ReCommentsAdapter(
    val postViewModel: PostViewModel,
    val listener: OnItemClickListener,
    val viewLifecycleOwner: LifecycleOwner,
    val checkReCommentUseCase: CheckReCommentUseCase,
    val lifecycleCoroutineScope: LifecycleCoroutineScope
) : ListAdapter<Comments, ReCommentsAdapter.ViewHolder>(itemCallback) {

    interface OnItemClickListener {
        fun onItemClick(recomments : Comments)
    }



    companion object {
        private val itemCallback = object : DiffUtil.ItemCallback<Comments>() {
            override fun areItemsTheSame(oldItem: Comments, newItem: Comments): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: Comments, newItem: Comments): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(val binding: ItemRecommentsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(element: Comments) {
            binding.recomments = element
            binding.viewmodel = postViewModel
            val user = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            lifecycleCoroutineScope.launch {
                checkReCommentUseCase(element)
                    .collect {
                        when (it) {
                            is Resource.Success -> {
                                val recomment = it.data
                                postViewModel.getReCommentLike(
                                    recomment!!.comments_likeCount.contains(
                                        user
                                    )
                                )
                                postViewModel.getReCommentLkeList(recomment!!.comments_likeCount)
                            }
                        }
                    }
            }

            binding.textViewItemRecommentsLike.setOnClickListener {
                listener.onItemClick(element)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRecommentsBinding.inflate(
            layoutInflater,
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = getItem(position)
        holder.bind(comment)
    }

}