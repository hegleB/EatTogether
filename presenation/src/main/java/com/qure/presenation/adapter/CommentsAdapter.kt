package com.qure.presenation.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.qure.domain.model.Comments
import com.qure.domain.usecase.CheckCommentUseCase
import com.qure.domain.usecase.CheckReCommentUseCase
import com.qure.domain.usecase.GetReCommentsUseCase
import com.qure.domain.usecase.UpdateRecommentLikeUseCase
import com.qure.domain.utils.Resource
import com.qure.presenation.R
import com.qure.presenation.databinding.ItemCommentsBinding
import com.qure.presenation.generated.callback.OnClickListener.Listener
import com.qure.presenation.viewmodel.PostViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CommentsAdapter(
    val itemClick: (Comments) -> Unit,
    val context : Context,
    val listener: OnItemClickListener,
    val postViewModel: PostViewModel,
    val viewLifecycleOwner: LifecycleOwner,
    val lifecycleCoroutineScope: LifecycleCoroutineScope,
    val checkCommentUseCase : CheckCommentUseCase,
    val getReCommentsUseCase: GetReCommentsUseCase,
    val checkReCommentUseCase: CheckReCommentUseCase

) : ListAdapter<Comments, CommentsAdapter.ViewHolder>(itemCallback), ReCommentsAdapter.OnItemClickListener {

    interface OnItemClickListener {
        fun onItemClick(comments : Comments)
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

    inner class ViewHolder(val binding: ItemCommentsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(element: Comments) {

            itemView.setOnClickListener{
                binding.comments?.run{
                    itemClick(this)
                }
            }

            binding.comments = element
            binding.viewmodel = postViewModel

            binding.textViewItemCommentsLike.setOnClickListener {
                listener.onItemClick(element)
            }

            val adapter = ReCommentsAdapter(postViewModel, this@CommentsAdapter ,viewLifecycleOwner,checkReCommentUseCase, lifecycleCoroutineScope)
            binding.recyclerViewItemCommentsRecomment.adapter = adapter

            FirebaseFirestore.getInstance().collectionGroup("reply")
                .whereEqualTo("comments_postkey", element.comments_postkey)
                .whereEqualTo("comments_depth", 1)
                .whereEqualTo("comments_commentskey", element.comments_commentskey)
                .orderBy("comments_timestamp", Query.Direction.ASCENDING)
                .orderBy("comments_replyTimeStamp", Query.Direction.ASCENDING)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val recomment = it.result.toObjects(Comments::class.java)
                        adapter.submitList(recomment)
                    }
                }
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

    override fun onItemClick(recomments: Comments) {
        postViewModel.checkReComment(recomments)
        postViewModel.updateReCommentLikeState()
    }

}