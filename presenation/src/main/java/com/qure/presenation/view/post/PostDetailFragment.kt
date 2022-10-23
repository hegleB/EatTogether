package com.qure.presenation.view.post

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.FirebaseFirestore
import com.quer.presenation.base.BaseFragment
import com.qure.domain.model.Comments
import com.qure.domain.usecase.CheckCommentUseCase
import com.qure.domain.usecase.CheckReCommentUseCase
import com.qure.domain.usecase.GetReCommentsUseCase
import com.qure.domain.usecase.UpdateRecommentLikeUseCase
import com.qure.presenation.R
import com.qure.presenation.adapter.CommentsAdapter
import com.qure.presenation.adapter.ReCommentsAdapter
import com.qure.presenation.databinding.FragmentPostDetailBinding
import com.qure.presenation.utils.BottomNavigationEvent
import com.qure.presenation.utils.KeyboardEvent
import com.qure.presenation.utils.OnBackPressedListener
import com.qure.presenation.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostDetailFragment : BaseFragment<FragmentPostDetailBinding>(R.layout.fragment_post_detail), CommentsAdapter.OnItemClickListener {

    private val postViewModel : PostViewModel by activityViewModels()

    @Inject
    lateinit var getReCommentsUseCase: GetReCommentsUseCase
    @Inject
    lateinit var updateRecommentLikeUseCase: UpdateRecommentLikeUseCase
    @Inject
    lateinit var checkCommentUseCase : CheckCommentUseCase
    @Inject
    lateinit var checkReCommentUseCase: CheckReCommentUseCase


    private val adapter : CommentsAdapter by lazy {
        CommentsAdapter(
            {
                val direction = PostDetailFragmentDirections.actionPostDetailFragmentToReCommentsFragment(it)
                findNavController().navigate(direction)
            },
            requireContext(),
            this,
            postViewModel,
            viewLifecycleOwner,
            viewLifecycleOwner.lifecycleScope,
            checkCommentUseCase,
            getReCommentsUseCase,
            checkReCommentUseCase
        )
    }

    private val args : PostDetailFragmentArgs by navArgs()

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

        postViewModel.editTextPostComment.observe(viewLifecycleOwner) {
            when (it.length) {
                0 -> binding.imageViewFragmentPostDetailSendComment.visibility = View.GONE
                else -> {
                    binding.imageViewFragmentPostDetailSendComment.visibility = View.VISIBLE

                }
            }
        }

        postViewModel.toolbarBack.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            findNavController().popBackStack()
            it.consume()
        }

        postViewModel.commentsList.observe(viewLifecycleOwner) {
            when (it.size) {
                0 -> {
                    binding.linearLayoutFragmentPostDetailNoComment.visibility = View.VISIBLE
                    binding.recyclerViewFragmentPostDetailComment.visibility = View.GONE
                }
                else -> {
                    binding.linearLayoutFragmentPostDetailNoComment.visibility = View.GONE
                    binding.recyclerViewFragmentPostDetailComment.visibility = View.VISIBLE
                }
            }


            adapter.submitList(it)

        }

        postViewModel.buttonSendComment.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            KeyboardEvent(requireContext()).hideKeyboard()
            postViewModel.editTextPostComment.value = ""
            it.consume()
        }

        postViewModel.likeList.observe(viewLifecycleOwner) {
            postViewModel.checkLike(it)
        }

    }

    override fun onItemClick(comments: Comments) {
        postViewModel.checkComment(comments.comments_commentskey)
        postViewModel.getCommentsKey(comments.comments_commentskey)
        postViewModel.updateCommentLikeState()
    }
}