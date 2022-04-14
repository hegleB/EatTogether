package com.qure.eattogether2.view.people

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.qure.eattogether2.R
import com.qure.eattogether2.adapter.ProfilePostAdapter
import com.qure.eattogether2.data.Comments
import com.qure.eattogether2.data.Post
import com.qure.eattogether2.data.User
import com.qure.eattogether2.databinding.FragmentProfileCommentPostBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileCommentPostFragment(val user: User) : Fragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    private lateinit var binding: FragmentProfileCommentPostBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile_comment_post,
            container,
            false
        )


        getPostInfo(user)
        return binding.root
    }


    fun getPostInfo(user: User) {
        val adapter = ProfilePostAdapter({ Post -> moveProfilePostToDetailPost(Post) }, firestore)
        val commentsPostList = mutableListOf<Post>()
        firestore.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING)
            .get().addOnCompleteListener { post ->
                firestore.collection("comments").whereEqualTo("comments_uid", user!!.uid)
                    .get().addOnCompleteListener { snapshot ->
                        val posts = post.result.toObjects(Post::class.java)
                        val commentsPost = snapshot.result.toObjects(Comments::class.java)
                        for (j in posts) {
                            for (i in commentsPost) {
                                if (j.key.equals(i.comments_postkey)) {
                                    commentsPostList.add(j)
                                }
                            }
                        }
                        adapter.submitList(commentsPostList)
                        binding.profileCommentPostRecyclerview.adapter = adapter
                    }
            }
    }
    fun moveProfilePostToDetailPost(post: Post) {

        val intet = Intent(requireActivity(), ProfileActivity::class.java)
        intet.putExtra("post",post)
        intet.putExtra("user",user)
        startActivity(intet)


    }


}