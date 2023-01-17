package com.qure.presenation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.qure.domain.model.Comments
import com.qure.domain.utils.*
import com.qure.presenation.databinding.ItemCommentsBinding
import com.qure.presenation.viewmodel.PostViewModel

class CommentsAdapter(
    val itemClick: (Comments) -> Unit,
    val context: Context,
    val postViewModel: PostViewModel,
    val viewLifecycleOwner: LifecycleOwner
) : ListAdapter<Comments, CommentsAdapter.ViewHolder>(itemCallback) {

    companion object {
        private val itemCallback = object : DiffUtil.ItemCallback<Comments>() {
            override fun areItemsTheSame(oldItem: Comments, newItem: Comments): Boolean {
                return oldItem.comments_commentskey == newItem.comments_commentskey
            }

            override fun areContentsTheSame(oldItem: Comments, newItem: Comments): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(val binding: ItemCommentsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val LIKE_COLOR = -807670
        private val UNLIKE_COLOR = -5131855

        fun bind(element: Comments) {
            binding.comments = element
            binding.viewmodel = postViewModel
            clickReComment()
            clickCommentLike(element)
            val adapter = ReCommentsAdapter(
                postViewModel,
                viewLifecycleOwner
            )
            getReComment(adapter, element)
        }

        private fun clickReComment() {
            binding.textViewItemCommentsRecomment.setOnClickListener {
                binding.comments?.apply {
                    itemClick(this)
                }
            }
        }

        private fun getReComment(
            adapter: ReCommentsAdapter,
            element: Comments
        ) {
            binding.recyclerViewItemCommentsRecomment.adapter = adapter
            binding.recyclerViewItemCommentsRecomment
            FirebaseFirestore.getInstance().collectionGroup(REPLY_COLLECTION_PATH)
                .whereEqualTo(COMMENTS_POST_KEY_FIELD, element.comments_postkey)
                .whereEqualTo(COMMENTS_DEPTH_FIELD, 1)
                .whereEqualTo(COMMENTS_COMMENT_KEY_FIELD, element.comments_commentskey)
                .orderBy(COMMENTS_TIMESTAMP_FIELD, Query.Direction.ASCENDING)
                .orderBy(COMMENTS_REPLY_TIMESTAMP_FIELD, Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        val recomment = snapshot.toObjects(Comments::class.java)
                        adapter.submitList(recomment)
                    }
                }
        }

        private fun clickCommentLike(element: Comments) {
            binding.textViewItemCommentsLike.setOnClickListener {
                when (binding.textViewItemCommentsLike.currentTextColor) {
                    LIKE_COLOR -> updateLikeCount(
                        element.comments_commentskey,
                        FieldValue.arrayRemove(postViewModel.currentUid)
                    )

                    UNLIKE_COLOR -> updateLikeCount(
                        element.comments_commentskey,
                        FieldValue.arrayUnion(postViewModel.currentUid)
                    )
                }
            }
        }

        private fun updateLikeCount(commentKey: String, fieldValue: FieldValue) {
            FirebaseFirestore.getInstance()
                .collection(COMMENTS_COLLECTION_PATH)
                .document(commentKey)
                .update(
                    "comments_likeCount",
                    fieldValue
                )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentsBinding.inflate(
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