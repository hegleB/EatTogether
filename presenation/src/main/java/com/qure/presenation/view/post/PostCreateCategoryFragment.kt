package com.qure.presenation.view.post

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.adapter.PostCreateCategotyAdapter
import com.qure.presenation.databinding.FragmentPostCreateCategoryBinding
import com.qure.presenation.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostCreateCategoryFragment :
    BaseFragment<FragmentPostCreateCategoryBinding>(R.layout.fragment_post_create_category) {

    private val postViewModel: PostViewModel by activityViewModels()
    private val adapter: PostCreateCategotyAdapter by lazy {
        PostCreateCategotyAdapter({
            postViewModel.getCategory(it)
            findNavController().popBackStack()
        }, resources.getStringArray(R.array.categey_name))
    }

    override fun init() {
        initViewModel()
        initAdapter()
        observeViewModel()
    }

    private fun initViewModel() {
        binding.viewmodel = postViewModel
    }

    private fun initAdapter() {
        binding.recyclerViewFragmentPostCreateCategory.adapter = adapter
    }

    private fun observeViewModel() {
        postViewModel.toolbarBack.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            findNavController().popBackStack()
            it.consume()
        }
    }
}
