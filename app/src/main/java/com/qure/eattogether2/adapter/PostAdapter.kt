package com.qure.eattogether2.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.qure.eattogether2.R
import com.qure.eattogether2.data.Post
import com.qure.eattogether2.data.PostImage

import com.qure.eattogether2.databinding.ItemPostBinding
import dagger.Module
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import javax.inject.Inject


class PostAdapter(val postClick : (Post) -> Unit): PagingDataAdapter<Post, PostAdapter.ViewHolder>(PostDiffUtil) {

    override fun onBindViewHolder(holder: PostAdapter.ViewHolder, position: Int) {
        val post = getItem(position)

        holder.bind(post!!)
    }

    inner class ViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {

            binding.post = post

            binding.root.setOnClickListener{
                postClick(post)

            }
            binding.postImage1Layout.clipToOutline=true


            if(post.postImages.size==1){

                binding.postImage1Layout.weightSum = 1F

                Glide.with(itemView.context).load(post.postImages.get(0)).into(binding.postImage1)

            }
            else if(post.postImages.size==2){
                binding.postImage2Layout.weightSum = 1F

                Glide.with(itemView.context).load(post.postImages.get(0)).into(binding.postImage1)
                Glide.with(itemView.context).load(post.postImages.get(1)).into(binding.postImage2)


            }
            else if(post.postImages.size==3){
                Glide.with(itemView.context).load(post.postImages.get(0)).into(binding.postImage1)
                Glide.with(itemView.context).load(post.postImages.get(1)).into(binding.postImage2)
                Glide.with(itemView.context).load(post.postImages.get(2)).into(binding.postImage3)
            } else {
                binding.postImages.visibility = View.GONE
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_post
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemPostBinding>(
            layoutInflater,
            viewType,
            parent,
            false
        )

        return ViewHolder(binding)
    }


    companion object PostDiffUtil : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }

    }
}

