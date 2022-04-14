package com.qure.eattogether2.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.qure.eattogether2.R
import com.qure.eattogether2.data.Comments
import com.qure.eattogether2.databinding.ItemCreateCategoryBinding
import com.qure.eattogether2.databinding.ItemRecommentsBinding

class ReCommentsAdapter(val context: Context ,val firestore: FirebaseFirestore) :
    PagingDataAdapter<Comments, ReCommentsAdapter.ViewHolder>(
        CommentsAdapter
    ) {

    inner class ViewHolder(val binding: ItemRecommentsBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun updateLikeCount(recomments: Comments, count: ArrayList<String>) {
            firestore.collectionGroup("reply").whereEqualTo("comments_postkey",recomments.comments_postkey)
                .whereEqualTo("comments_depth",1)
                .whereEqualTo("comments_commentskey", recomments.comments_commentskey)
                .whereEqualTo("comments_timestamp", recomments.comments_timestamp)
                .whereEqualTo("comments_replyTimeStamp", recomments.comments_replyTimeStamp)
                .get()
                .addOnSuccessListener { snapshots ->
                    val snapshot = snapshots.documents.get(0)
                    snapshot.reference.update("comments_likeCount", count)

            }
            binding.recommentsLikeCount.text = count.size.toString()


        }

        fun bind(recomments: Comments) {

            binding.recomments = recomments
            val user = FirebaseAuth.getInstance().currentUser!!.uid
            if (recomments.comments_likeCount.contains(user)) {
                binding.recommentsLike.setTextColor(ContextCompat.getColor(context, R.color.like_color))
            } else {
                binding.recommentsLike.setTextColor(Color.GRAY)
            }


            binding.recommentsLike.setOnClickListener {

                if (!recomments.comments_likeCount.contains(user)) {
                    recomments.comments_likeCount.add(user)
                    binding.recommentsLike.setTextColor(ContextCompat.getColor(context, R.color.like_color))
                    val count = recomments.comments_likeCount
                    updateLikeCount(recomments, count)

                } else {
                    recomments.comments_likeCount.remove(user)
                    binding.recommentsLike.setTextColor(Color.GRAY)
                    val count = recomments.comments_likeCount
                    updateLikeCount(recomments, count)

                }

            }


        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRecommentsBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReCommentsAdapter.ViewHolder, position: Int) {
        val reply = getItem(position)

        holder.bind(reply!!)
    }
}