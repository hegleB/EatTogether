package com.qure.presenation.view.post

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.quer.presenation.base.BaseFragment
import com.qure.domain.model.Comments
import com.qure.domain.usecase.comment.CheckCommentUseCase
import com.qure.domain.usecase.comment.CheckReCommentUseCase
import com.qure.domain.usecase.comment.GetReCommentsUseCase
import com.qure.domain.usecase.comment.UpdateRecommentLikeUseCase
import com.qure.presenation.R
import com.qure.presenation.adapter.CommentsAdapter
import com.qure.presenation.databinding.FragmentPostDetailBinding
import com.qure.presenation.utils.BottomNavigationEvent
import com.qure.presenation.utils.KeyboardEvent
import com.qure.presenation.utils.OnBackPressedListener
import com.qure.presenation.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostDetailFragment : BaseFragment<FragmentPostDetailBinding>(R.layout.fragment_post_detail) {

    private val postViewModel: PostViewModel by activityViewModels()

    @Inject
    lateinit var getReCommentsUseCase: GetReCommentsUseCase

    @Inject
    lateinit var updateRecommentLikeUseCase: UpdateRecommentLikeUseCase

    @Inject
    lateinit var checkCommentUseCase: CheckCommentUseCase

    @Inject
    lateinit var checkReCommentUseCase: CheckReCommentUseCase


    private val adapter: CommentsAdapter by lazy {
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
        }

    }

    private fun initPost() {
        binding.post = args.post

    }

    private fun initAdapter() {
        binding.recyclerViewFragmentPostDetailComment.adapter = adapter
    }

    private fun observeViewModel() {
        postViewModel.toolbarBack.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            findNavController().popBackStack()
            it.consume()
        }

        postViewModel.commentsList.observe(viewLifecycleOwner) {
            postViewModel.updateCommentsCount(it.size.toString())
            adapter.submitList(it)

        }

        postViewModel.buttonSendComment.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            KeyboardEvent(requireContext()).hideKeyboard()
            postViewModel.editTextPostComment.value = ""
            it.consume()
        }
    }
}