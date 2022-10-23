package com.qure.presenation.view.post

import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.quer.presenation.base.BaseFragment
import com.qure.domain.model.Comments
import com.qure.domain.usecase.CheckReCommentUseCase
import com.qure.presenation.R
import com.qure.presenation.adapter.ReCommentsAdapter
import com.qure.presenation.databinding.FragmentReCommentsBinding
import com.qure.presenation.utils.KeyboardEvent
import com.qure.presenation.utils.OnBackPressedListener
import com.qure.presenation.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReCommentsFragment : BaseFragment<FragmentReCommentsBinding>(R.layout.fragment_re_comments), ReCommentsAdapter.OnItemClickListener {

    @Inject
    lateinit var checkReCommentUseCase: CheckReCommentUseCase

    private val args : ReCommentsFragmentArgs by navArgs()
    private val postViewModel : PostViewModel by activityViewModels()
    private val adapter : ReCommentsAdapter by lazy {
        ReCommentsAdapter(
            postViewModel,
            this,
            viewLifecycleOwner,
            checkReCommentUseCase,
            viewLifecycleOwner.lifecycleScope
        )
    }

    override fun init() {
        OnBackPressedListener().back(activity!!, findNavController())
        initViewModel()
        initAdapter()
        observeViewModel()
    }

    private fun initViewModel() {
        binding.recomments = args.recomment
        binding.viewmodel = postViewModel

        postViewModel.apply {
            getReCommentLkeList(args.recomment.comments_likeCount)
            getReCommentLike(args.recomment.comments_likeCount.contains(postViewModel.currentUid))
            getComments()
            getCommentsKey(args.recomment.comments_commentskey)
            checkComment(args.recomment.comments_commentskey)
            checkReComment(args.recomment)
            getReComments(args.recomment)
            getUserInfo()

        }
    }

    private fun observeViewModel() {
        postViewModel.editTextPostComment.observe(viewLifecycleOwner) {
            when (it.length) {
                0 -> binding.imageViewFragmentReCommentSendComments.visibility = View.GONE
                else -> {
                    binding.imageViewFragmentReCommentSendComments.visibility = View.VISIBLE

                }
            }
        }

        postViewModel.toolbarBack.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            findNavController().popBackStack()
            it.consume()
        }

        postViewModel.recommentsList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        postViewModel.buttonSendReComment.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            KeyboardEvent(requireContext()).hideKeyboard()
            postViewModel.editTextPostComment.value =  ""
            it.consume()
        }

        postViewModel.commentsLikeList.observe(viewLifecycleOwner) {
            postViewModel.checkCommentLike(it)
        }


    }

    private fun initAdapter() {
        binding.recyclerViewFragmentReComment.adapter = adapter
    }

    override fun onItemClick(recomments: Comments) {
        postViewModel.checkReComment(recomments)
        postViewModel.updateReCommentLikeState()
    }


}