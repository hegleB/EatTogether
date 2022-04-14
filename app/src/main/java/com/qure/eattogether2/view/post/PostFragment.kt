package com.qure.eattogether2.view.post

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.eattogether2.R
import com.qure.eattogether2.adapter.CreatePostCategoryAdapter
import com.qure.eattogether2.adapter.PostAdapter
import com.qure.eattogether2.adapter.PostCategoryAdapter
import com.qure.eattogether2.data.Comments
import com.qure.eattogether2.data.Post
import com.qure.eattogether2.data.User
import com.qure.eattogether2.databinding.FragmentPostBinding
import com.qure.eattogether2.paging.PostPagingSource
import com.qure.eattogether2.repository.PostRepository
import com.qure.eattogether2.view.people.PeopleFragment
import com.qure.eattogether2.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PostFragment : Fragment() {

    @Inject
    lateinit var postRepository: PostRepository

    @Inject
    lateinit var firestore: FirebaseFirestore

    private lateinit var binding: FragmentPostBinding
    private val viewModel by viewModels<PostViewModel>()
    private lateinit var navBar: BottomNavigationView
    val adapter = PostAdapter({ Post -> movePostToDetailPost(Post) })
    private lateinit var categoryList : Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        categoryList = resources.getStringArray(R.array.categey_name)
        val categoryadapter = PostCategoryAdapter(categoryList)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post, container, false)



        binding.apply {
            postSwipe.setOnRefreshListener {
                adapter.refresh()


                postSwipe.isRefreshing = false
            }
        }

        getAllPost()
        setProgressBarAccordingToLoadState()
        movePostToCreatePost()
        setCategoryRecyclerview(categoryadapter)
        moveToPostCategoryFragment(categoryadapter)




        return binding.root

    }

    fun movePostToDetailPost(post: Post) {

        post.run {
            val moveToDetail =
                PostFragmentDirections.actionPostContainerFragmentToDetailPostFragment(
                    title,
                    timestamp,
                    userimage,
                    content,
                    category,
                    likecount.toTypedArray(),
                    arrayOf(),
                    writer,
                    key,
                    "post",
                    User()
                )

            findNavController().navigate(moveToDetail)
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)

        navBar = requireActivity().findViewById(R.id.bottom_navigation)
        navBar.menu.findItem(R.id.postFragment)
            .setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener {
                override fun onMenuItemClick(p0: MenuItem?): Boolean {
                    binding.postRecyclerview.smoothScrollToPosition(0)

                    return false
                }

            })

    }

    fun setCategoryRecyclerview(categoryAdapter: PostCategoryAdapter) {


        binding.postCategoryRecyclerView.adapter = categoryAdapter
        binding.postCategoryRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)

    }


    fun moveToPostCategoryFragment(categoryAdapter: PostCategoryAdapter){



        categoryAdapter.setItemClickListener(object : PostCategoryAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int) {

                val directions = PostFragmentDirections.actionPostContainerFragmentToPostCategoryFragment(
                    categoryList[position]
                )

                viewModel.categoryName = categoryList[position]

                findNavController().navigate(directions)
            }

        })
    }



    private fun getAllPost() {

        viewModel.allPosts.observe(viewLifecycleOwner, { post ->

            lifecycleScope.launch {
                viewModel.getPosts.collectLatest {
                    adapter.submitData(it)
                }
            }

        })
        binding.postRecyclerview.adapter = adapter
    }

    private fun setProgressBarAccordingToLoadState() {

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                binding.postProgressbar.isVisible = it.append is LoadState.Loading
            }
        }
    }

    private fun movePostToCreatePost() {
        binding.postAdd.setOnClickListener {
            findNavController().navigate(R.id.action_postContainerFragment_to_createPostFragment)
        }
    }

}