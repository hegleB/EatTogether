package com.qure.eattogether2.adapter


import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.eattogether2.R
import com.qure.eattogether2.bindingadapter.ImageBindingAdapter
import com.qure.eattogether2.bindingadapter.TextBindingAdapter
import com.qure.eattogether2.data.ChatComment
import com.qure.eattogether2.data.Comments
import com.qure.eattogether2.data.Post
import com.qure.eattogether2.databinding.ItemChatLeftBinding
import com.qure.eattogether2.databinding.ItemChatRightBinding
import com.qure.eattogether2.databinding.ItemPostBinding
import com.qure.eattogether2.databinding.ItemProfileViewpagerBinding
import com.qure.eattogether2.view.chat.*
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.android.synthetic.main.item_chat_left.view.*
import kotlinx.android.synthetic.main.item_chat_right.view.*
import kotlinx.android.synthetic.main.item_message.view.*
import kotlinx.android.synthetic.main.item_message.view.divider
import kotlinx.android.synthetic.main.item_message.view.divider_date
import kotlinx.android.synthetic.main.item_message.view.msg_item
import kotlinx.android.synthetic.main.item_message.view.timestamp
import kotlinx.android.synthetic.main.item_message.view.img_item
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class ProfilePostAdapter(val postClick : (Post) -> Unit, val firestore: FirebaseFirestore): ListAdapter<Post, ProfilePostAdapter.ViewHolder>(ProfilePostDiffUtil) {
    companion object ProfilePostDiffUtil : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem==newItem
        }

    }

    override fun onBindViewHolder(holder: ProfilePostAdapter.ViewHolder, position: Int) {
        val post = getItem(position)

        holder.bind(post!!)
    }

    inner class ViewHolder(private val binding: ItemProfileViewpagerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {

            binding.profilePost = post

            binding.apply {
                if (post.postImages.size >= 1) {
                    postViewpagerImage.visibility = View.VISIBLE
                    Glide.with(postViewpagerImage.context).load(post.postImages.get(0))
                        .into(postViewpagerImage)


                } else {
                    postViewpagerImage.visibility = View.GONE
                }

                firestore.collection("posts").document(post.key).get().addOnCompleteListener{
                    key ->
                    val posts = key.result.toObject(Post::class.java)
                    postViewpagerLike.setText("좋아요 "+ posts!!.likecount.size)

                }

                firestore.collection("comments").whereEqualTo("comments_postkey",post.key).addSnapshotListener{
                    comments, e ->
                    val comments = comments!!.toObjects(Comments::class.java)

                    postViewpagerComment.setText("댓글 "+ comments.size)
                }


            }



            binding.root.setOnClickListener {
                postClick(post)

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_profile_viewpager
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemProfileViewpagerBinding>(
            layoutInflater,
            viewType,
            parent,
            false
        )

        return ViewHolder(binding)
    }
}

