package com.qure.eattogether2.view.people

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.qure.eattogether2.R
import com.qure.eattogether2.adapter.ProfilePostAdapter
import com.qure.eattogether2.data.Post
import com.qure.eattogether2.data.User
import com.qure.eattogether2.databinding.FragmentProfileWritePostBinding
import com.qure.eattogether2.view.home.HomeActivity
import com.qure.eattogether2.view.post.PostContainerFragment
import com.qure.eattogether2.view.post.PostFragmentDirections
import com.qure.eattogether2.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ProfileWritePostFragment(val user: User) : Fragment() {

    @Inject
    lateinit var firestore : FirebaseFirestore


    private lateinit var binding : FragmentProfileWritePostBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_write_post, container, false)


        getPostInfo(user)




        return binding.root
    }

    fun moveProfilePostToDetailPost(post: Post) {

        val intet = Intent(requireActivity(), ProfileActivity::class.java)
        intet.putExtra("post",post)
        intet.putExtra("user",user)
        startActivity(intet)


    }


    fun getPostInfo(user : User){
        val adapter = ProfilePostAdapter({ Post -> moveProfilePostToDetailPost(Post) }, firestore)
        firestore.collection("posts").whereEqualTo("uid", user.uid).orderBy("timestamp",Query.Direction.DESCENDING).get()
            .addOnCompleteListener { snapshot ->
                if (snapshot.isSuccessful) {
                    val profileWritePost = snapshot.result.toObjects(Post::class.java)

                    adapter.submitList(profileWritePost)

                    binding.profileWritePostRecyclerview.adapter = adapter

                }
            }
    }

}