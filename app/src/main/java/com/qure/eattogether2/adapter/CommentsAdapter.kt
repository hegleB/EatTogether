package com.qure.eattogether2.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.qure.eattogether2.R
import com.qure.eattogether2.data.Comments
import com.qure.eattogether2.databinding.ItemCommentsBinding
import com.qure.eattogether2.repository.PostRepository
import com.qure.eattogether2.view.post.DetailPostFragmentDirections
import com.qure.eattogether2.viewmodel.PostViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class CommentsAdapter(
    val context: Context, val firestore: FirebaseFirestore, val viewModel: PostViewModel,
    val postRepository: PostRepository
) :
    PagingDataAdapter<Comments, CommentsAdapter.ViewHolder>(CommentsDiffUtil) {


    companion object CommentsDiffUtil : DiffUtil.ItemCallback<Comments>() {
        override fun areItemsTheSame(oldItem: Comments, newItem: Comments): Boolean {
            return oldItem.comments_uid == newItem.comments_uid
        }

        override fun areContentsTheSame(oldItem: Comments, newItem: Comments): Boolean {
            return oldItem == newItem
        }

    }


    inner class ViewHolder(val binding: ItemCommentsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun updateLikeCount(comment: Comments ,count : ArrayList<String>) {
            firestore.collection("comments").document(comment.comments_commentskey).update("comments_likeCount",count)
            binding.commentsLikeCount.text = count.size.toString()
        }

        fun bind(comment: Comments) {

            binding.apply {
                comments = comment
                val adapter = ReCommentsAdapter(context,firestore)
                var commentsList: List<Comments> = listOf()
                var liveData = MutableLiveData<List<Comments>>()

                firestore.collectionGroup("reply")
                    .whereEqualTo("comments_postkey", comment.comments_postkey)
                    .whereEqualTo("comments_depth", 1)
                    .whereEqualTo("comments_commentskey", comment.comments_commentskey)
                    .orderBy("comments_timestamp", Query.Direction.ASCENDING)
                    .orderBy("comments_replyTimeStamp", Query.Direction.ASCENDING)
                    .get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val result = task.result.toObjects(Comments::class.java)

                            CoroutineScope(Dispatchers.IO).launch {
                                commentsList = result

                                liveData.postValue(commentsList)
                                viewModel.getReply(liveData).collectLatest {
                                    adapter.submitData(it)

                                }
                            }
                            binding.commentsRecommentRecyclerview.adapter = adapter

                            val manager = LinearLayoutManager(context)
                            manager.reverseLayout = true
                            manager.stackFromEnd = true

                            binding.commentsRecommentRecyclerview.layoutManager = manager
                        }
                    }





                val user = FirebaseAuth.getInstance().currentUser!!.uid
                if(comment.comments_likeCount.contains(user)){
                    binding.commentsLike.setTextColor(ContextCompat.getColor(context, R.color.like_color))
                } else {
                    binding.commentsLike.setTextColor(Color.GRAY)
                }


                binding.commentsLike.setOnClickListener{

                    if(!comment.comments_likeCount.contains(user)){
                        comment.comments_likeCount.add(user)
                        binding.commentsLike.setTextColor(ContextCompat.getColor(context, R.color.like_color))
                        val count = comment.comments_likeCount
                        updateLikeCount(comment ,count)

                    } else {
                        comment.comments_likeCount.remove(user)
                        binding.commentsLike.setTextColor(Color.GRAY)
                        val count = comment.comments_likeCount
                        updateLikeCount(comment ,count)

                    }

                }



                binding.commentsRecomment.setOnClickListener { view ->

                    val direction =
                        DetailPostFragmentDirections.actionDetailPostFragmentToReCommentsFragment(
                            comment.comments_usernm,
                            comment.comments_userimage,
                            comment.comments_timestamp,
                            comment.comments_content,
                            comment.comments_likeCount.toTypedArray(),
                            comment.comments_postkey,
                            commentsList.toTypedArray(),
                            comment.comments_commentskey,
                            comment.comments_uid

                        )

                    view.findNavController().navigate(direction)

                }


            }

        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentsBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comments = getItem(position)
        holder.bind(comments!!)

    }

}