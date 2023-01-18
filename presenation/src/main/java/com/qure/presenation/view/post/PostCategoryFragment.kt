package com.qure.presenation.view.post

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.adapter.PostAdapter
import com.qure.presenation.databinding.FragmentPostCategoryBinding
import com.qure.presenation.utils.OnBackPressedListener
import com.qure.presenation.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostCategoryFragment :
    BaseFragment<FragmentPostCategoryBinding>(R.layout.fragment_post_category) {

    private val postViewModel: PostViewModel by activityViewModels()
    private val args: PostCategoryFragmentArgs by navArgs()
    private val postAdapter: PostAdapter by lazy {
        PostAdapter {
            val direction =
                PostCategoryFragmentDirections.actionPostCategoryFragmentToPostDetailFragment(it)
            findNavController().navigate(direction)
        }
    }

    override fun init() {
        OnBackPressedListener().back(requireActivity(), findNavController())
        initViewModel()
        initAdapter()
        observeViewModel()
    }

    private fun initViewModel() {
        binding.viewmodel = postViewModel
        postViewModel.getCategoryName(args.categoryname)
        postViewModel.getCategoryPost()
    }

    private fun initAdapter() {
        binding.recyclerViewFragmentPostCategory.adapter = postAdapter
    }

    private fun observeViewModel() {
        postViewModel.categoryPostList.observe(viewLifecycleOwner) {
            postAdapter.submitList(it)
        }
    }
}
