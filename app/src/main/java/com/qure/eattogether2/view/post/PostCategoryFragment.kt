package com.qure.eattogether2.view.post

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.eattogether2.R
import com.qure.eattogether2.adapter.PostAdapter
import com.qure.eattogether2.data.Post
import com.qure.eattogether2.data.User
import com.qure.eattogether2.databinding.FragmentPostCategoryBinding
import com.qure.eattogether2.paging.PostPagingSource
import com.qure.eattogether2.repository.PostRepository
import com.qure.eattogether2.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PostCategoryFragment : Fragment() {


    @Inject
    lateinit var postRepository: PostRepository
    @Inject
    lateinit var firestore: FirebaseFirestore

    private lateinit var binding : FragmentPostCategoryBinding
    private val args by navArgs<PostCategoryFragmentArgs>()
    val adapter = PostAdapter({ Post -> movePostToDetailPost(Post) })
    private lateinit var navBar: BottomNavigationView
    private var allCategoryPosts = MutableLiveData<MutableList<Post>>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        navBar = requireActivity().findViewById(R.id.bottom_navigation)
        navBar.visibility = View.GONE


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post_category, container, false)

        getAllCategoryPost()
        setCategoryPostToolbar()

        binding.categoryName =  Post(
            "",
            "",
            "",
            args.categoryname,
            "",
            "",
            "",
            "",
            arrayListOf(),
            "",
        )


        return binding.root
    }

    fun setCategoryPostToolbar() {
        binding.apply {
            categoryPostToolbar.setNavigationIcon(R.drawable.ic_back)
            categoryPostToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
        }

    }


    private fun getAllCategoryPost() {



        CoroutineScope(Dispatchers.Main).launch {
            postRepository.getCategoryPosts(args.categoryname).collectLatest {

                allCategoryPosts.postValue(it.toMutableList())

                val getCategoryPosts = Pager(
                    PagingConfig(
                        pageSize = 30
                    )
                ) {
                    PostPagingSource(allCategoryPosts)
                }.flow.cachedIn(lifecycleScope)


                getCategoryPosts.collectLatest {
                    adapter.submitData(it)
                }


            }
        }

        binding.categoryPostRecyclerview.adapter =adapter

    }


    fun movePostToDetailPost(post: Post) {

        post.run {
            val moveToDetail =
                PostCategoryFragmentDirections.actionPostCategoryFragmentToDetailPostFragment(
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

    override fun onDestroy() {
        super.onDestroy()
        navBar = requireActivity().findViewById(R.id.bottom_navigation)
        navBar.visibility = View.VISIBLE
    }

}