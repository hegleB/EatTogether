package com.qure.presenation.view.people

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.adapter.ProfilePostAdapter
import com.qure.presenation.databinding.FragmentPostWriteTabBinding
import com.qure.presenation.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostWriteTabFragment(val uid: String) :
    BaseFragment<FragmentPostWriteTabBinding>(R.layout.fragment_post_write_tab) {

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
        postsViewModel.getProfileCreatedPosts()
    }

    private fun initAdapter() {
        binding.recyclerViewFragmentTabPostWrite.adapter = profilePostAdapter
    }

    private fun observeViewModel() {
        postsViewModel.profileCreatedPost.observe(viewLifecycleOwner) {
            profilePostAdapter.submitList(it)
        }
    }

}