package com.qure.presenation.view.people

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.adapter.ProfilePostAdapter
import com.qure.presenation.databinding.FragmentPostLikeTabBinding
import com.qure.presenation.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostLikeTabFragment(val uid: String) :
    BaseFragment<FragmentPostLikeTabBinding>(R.layout.fragment_post_like_tab) {

    private val postsViewModel: PostViewModel by activityViewModels()
    private val profilePostAdapter: ProfilePostAdapter by lazy {
        ProfilePostAdapter({
            val direction =
                ProfileDetailFragmentDirections.actionProfileDetailFragmentToProfilePostDetailFragment(
                    it
                )
            findNavController().navigate(direction)
        }, postsViewModel)
    }

    override fun init() {
        initViewModel()
        initAdapter()
        observeViewModel()
    }

    private fun initViewModel() {
        postsViewModel.getProfileUid(uid)
        postsViewModel.getProfileLikedPosts()
    }

    private fun initAdapter() {
        binding.recyclerViewFragmentTabPostLike.adapter = profilePostAdapter
    }

    private fun observeViewModel() {
        postsViewModel.profileLikedPost.observe(viewLifecycleOwner) {
            profilePostAdapter.submitList(it)
        }
    }

}