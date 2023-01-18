package com.qure.presenation.view.post

import android.net.Uri
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.gun0912.tedpermission.PermissionListener
import com.quer.presenation.base.BaseFragment
import com.qure.domain.utils.Resource
import com.qure.presenation.utils.BottomImagePicker
import com.qure.presenation.R
import com.qure.presenation.adapter.PostCreateImageAdapter
import com.qure.presenation.databinding.FragmentPostCreateBinding
import com.qure.presenation.utils.BottomNavigationEvent
import com.qure.presenation.utils.OnBackPressedListener
import com.qure.presenation.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedbottompicker.TedBottomSheetDialogFragment

@AndroidEntryPoint
class PostCreateFragment : BaseFragment<FragmentPostCreateBinding>(R.layout.fragment_post_create) {

    private val postViewModel: PostViewModel by activityViewModels()
    private val adapter: PostCreateImageAdapter by lazy {
        PostCreateImageAdapter(postViewModel, viewLifecycleOwner)
    }
    private val bottomImagePicker by lazy {
        BottomImagePicker(requireContext(), requireActivity())
    }

    override fun init() {
        BottomNavigationEvent().hideBottomNavigation(activity!!)
        initViewModel()
        initAdapter()
        observeViewModel()
        menuItemClick()
        OnBackPressedListener().back(requireActivity(), findNavController())
    }

    private fun initViewModel() {
        binding.viewmodel = postViewModel
        postViewModel.apply {
            getUserInfo()
        }
    }

    private fun initAdapter() {
        binding.recyclerViewFragmentPostCreateImage.adapter = adapter
    }

    private fun observeViewModel() {
        postViewModel.toolbarBack.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            findNavController().popBackStack()
            it.consume()
        }

        postViewModel.buttonUploadImage.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            requestPermission()
            it.consume()
        }

        postViewModel.buttonCategory.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            findNavController().navigate(R.id.action_postCreateFragment_to_postCreateCategoryFragment)
            it.consume()
        }

        postViewModel.createPostImage.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        postViewModel.updatedState.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    findNavController().popBackStack()
                    postViewModel.setUpdatedState(Resource.Empty(""))
                }
            }
        }

    }

    private fun menuItemClick() {
        binding.toolBarFragmentPostCreate.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_create -> {
                    postViewModel.createPost()
                    true
                }
                else -> false
            }
        }
    }

    private fun requestPermission() {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                openImagePicker()
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                showPermissionSnackBar(deniedPermissions)
            }
        }
        bottomImagePicker.setPermission(permissionListener)
    }

    private fun showPermissionSnackBar(deniedPermissions: ArrayList<String>?) {
        bottomImagePicker.showSnackBarMessage(
            binding.constraintLayoutFragmentPostCreate,
            deniedPermissions ?: arrayListOf()
        )
    }

    private fun openImagePicker() {
        bottomImagePicker.openImagePicker("3개만 선택이 가능합니다.", "선택")
            .showMultiImage { uriList ->
                val list = getImageUri(uriList)
                postViewModel.getPostCreateImage(list)
            }
    }

    private fun getImageUri(uriList: MutableList<Uri>): ArrayList<String> {
        val list = arrayListOf<String>()
        for (uri in uriList) {
            list.add(uri.toString())
        }
        return list
    }
}