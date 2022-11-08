package com.qure.presenation.view.post

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.adapter.PostAdapter
import com.qure.presenation.adapter.PostCategoryAdapter
import com.qure.presenation.databinding.FragmentPostBinding
import com.qure.presenation.utils.BottomNavigationEvent
import com.qure.presenation.utils.OnBackPressedListener
import com.qure.presenation.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostFragment : BaseFragment<FragmentPostBinding>(R.layout.fragment_post) {

    private val postViewModel: PostViewModel by activityViewModels()
    private val postAdapter: PostAdapter by lazy {
        PostAdapter {
            val direction = PostFragmentDirections.actionPostFragmentToPostDetailFragment(it)
            findNavController().navigate(direction)
        }
    }
    private val postCategoryAdapter: PostCategoryAdapter by lazy {
        PostCategoryAdapter(resources.getStringArray(R.array.categey_name),
            {
                val direction = PostFragmentDirections.actionPostFragmentToPostCategoryFragment(it)
                findNavController().navigate(direction)
            })
    }

    override fun init() {
        BottomNavigationEvent().showBottomNavigation(activity!!)
        OnBackPressedListener().finish(requireActivity(), requireActivity())
        initViewModel()
        observeViewModel()
        initAdapter()
    }

    private fun initViewModel() {
        binding.viewmodel = postViewModel
        postViewModel.getAllPost()
    }

    private fun initAdapter() {
        binding.apply {
            recyclerViewFragmentPostPost.adapter = postAdapter
            recyclerViewFragmentPostCategory.adapter = postCategoryAdapter
        }
    }

    private fun observeViewModel() {

        postViewModel.postList.observe(viewLifecycleOwner) {
            postAdapter.submitList(it)
        }

        postViewModel.postCreate.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            findNavController().navigate(R.id.action_postFragment_to_postCreateFragment)
            it.consume()
        }
    }
}