package com.qure.presenation.view.post

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.adapter.CommentsAdapter
import com.qure.presenation.adapter.PostImagesAdapter
import com.qure.presenation.databinding.FragmentPostDetailBinding
import com.qure.presenation.utils.BottomNavigationEvent
import com.qure.presenation.utils.KeyboardEvent
import com.qure.presenation.utils.OnBackPressedListener
import com.qure.presenation.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostDetailFragment : BaseFragment<FragmentPostDetailBinding>(R.layout.fragment_post_detail) {

    private val postViewModel: PostViewModel by activityViewModels()

    private val commentAdapter: CommentsAdapter by lazy {
        CommentsAdapter(
            {
                val direction =
                    PostDetailFragmentDirections.actionPostDetailFragmentToReCommentsFragment(it)
                findNavController().navigate(direction)
            },
            requireContext(),
            postViewModel,
            viewLifecycleOwner
        )
    }

    private val imageAdapter: PostImagesAdapter by lazy {
        PostImagesAdapter()
    }

    private val args: PostDetailFragmentArgs by navArgs()

    override fun init() {
        BottomNavigationEvent().hideBottomNavigation(activity!!)
        OnBackPressedListener().back(requireActivity(), findNavController())
        initPost()
    }

    override fun onStart() {
        super.onStart()
        initViewModel()
        observeViewModel()
        initAdapter()
    }

    private fun initViewModel() {
        binding.viewmodel = postViewModel
        postViewModel.apply {
            getPostList(args.post.postImages)
            getPostKey(args.post.key)
            getComments()
            getUserInfo()
            checkPost()
            getPostImages()
        }

    }

    private fun initPost() {
        binding.post = args.post

    }

    private fun initAdapter() {
        binding.recyclerViewFragmentPostDetailComment.adapter = commentAdapter
        binding.recyclerViewFragmentPostDetailImage.adapter = imageAdapter
    }

    private fun observeViewModel() {
        postViewModel.toolbarBack.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            findNavController().popBackStack()
            it.consume()
        }

        postViewModel.commentsList.observe(viewLifecycleOwner) {
            postViewModel.updateCommentsCount(it.size.toString())
            commentAdapter.submitList(it)

        }

        postViewModel.postDetailImageList.observe(viewLifecycleOwner) {
            imageAdapter.submitList(it)
        }

        postViewModel.buttonSendComment.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            KeyboardEvent(requireContext()).hideKeyboard()
            postViewModel.editTextPostComment.value = ""
            it.consume()
        }
    }
}