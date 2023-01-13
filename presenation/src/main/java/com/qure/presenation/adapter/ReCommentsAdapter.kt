package com.qure.presenation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.model.Comments
import com.qure.domain.usecase.comment.CheckReCommentUseCase
import com.qure.domain.utils.Constants
import com.qure.domain.utils.Resource
import com.qure.presenation.R
import com.qure.presenation.databinding.ItemRecommentsBinding
import com.qure.presenation.viewmodel.PostViewModel
import kotlinx.coroutines.launch

class ReCommentsAdapter(
    val postViewModel: PostViewModel,
    val viewLifecycleOwner: LifecycleOwner
) : ListAdapter<Comments, ReCommentsAdapter.ViewHolder>(itemCallback) {

    companion object {
        private val itemCallback = object : DiffUtil.ItemCallback<Comments>() {
            override fun areItemsTheSame(oldItem: Comments, newItem: Comments): Boolean {
                return oldItem.comments_replyKey == newItem.comments_replyKey
            }

            override fun areContentsTheSame(oldItem: Comments, newItem: Comments): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(val binding: ItemRecommentsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val LIKE_COLOR = -807670
        private val UNLIKE_COLOR = -5131855

        fun bind(element: Comments) {
            binding.recomments = element
            binding.viewmodel = postViewModel
            clickReCommentLike(element)
        }

        private fun clickReCommentLike(element: Comments) {
            binding.textViewItemRecommentsLike.setOnClickListener {
                when (binding.textViewItemRecommentsLike.currentTextColor) {
                    LIKE_COLOR -> updateLikeCount(
                        element.comments_commentskey,
                        element.comments_replyKey,
                        FieldValue.arrayRemove(postViewModel.currentUid)
                    )
                    UNLIKE_COLOR -> updateLikeCount(
                        element.comments_commentskey,
                        element.comments_replyKey,
                        FieldValue.arrayUnion(postViewModel.currentUid)
                    )
                }
            }
        }

        private fun updateLikeCount(commentKey: String, recommentKey: String, fieldValue: FieldValue) {
            FirebaseFirestore.getInstance()
                .collection(Constants.COMMENTS_COLLECTION_PATH)
                .document(commentKey)
                .collection(Constants.REPLY_COLLECTION_PATH)
                .document(recommentKey)
                .update(
                    "comments_likeCount",
                    fieldValue
                )
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