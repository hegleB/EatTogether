package com.qure.presenation.view.post

import android.net.Uri
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.quer.presenation.base.BaseFragment
import com.qure.domain.utils.Resource
import com.qure.presenation.R
import com.qure.presenation.adapter.PostCreateImageAdapter
import com.qure.presenation.databinding.FragmentPostCreateBinding
import com.qure.presenation.utils.BottomNavigationEvent
import com.qure.presenation.utils.OnBackPressedListener
import com.qure.presenation.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedbottompicker.TedBottomPicker
import gun0912.tedbottompicker.TedBottomSheetDialogFragment

@AndroidEntryPoint
class PostCreateFragment : BaseFragment<FragmentPostCreateBinding>(R.layout.fragment_post_create) {

    private val postViewModel: PostViewModel by activityViewModels()
    private val adapter: PostCreateImageAdapter by lazy {
        PostCreateImageAdapter(postViewModel, viewLifecycleOwner)
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
            adapter.notifyDataSetChanged()
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

            override fun onPermissionDenied(deniedPermissions: java.util.ArrayList<String>?) {
                Snackbar.make(
                    binding.constraintLayoutFragmentPostCreate, "Permission Denied\n" +
                            deniedPermissions.toString(), Snackbar.LENGTH_LONG
                ).show()
            }

        }
        TedPermission.with(requireContext())
            .setPermissionListener(permissionListener)
            .setRationaleMessage("사진을 추가하기 위해서는 권한 설정이 필요합니다.")
            .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다..")
            .setPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check()
    }

    private fun openImagePicker() {
        TedBottomPicker.with(requireActivity())
            .setPeekHeight(1600)
            .showGalleryTile(false)
            .setPreviewMaxCount(1000)
            .setSelectMaxCount(3)
            .setSelectMaxCountErrorText("3개만 선택이 가능합니다.")
            .showTitle(false)
            .setTitleBackgroundResId(R.color.light_red)
            .setGalleryTileBackgroundResId(R.color.white)
            .setCompleteButtonText("선택")
            .setEmptySelectionText("사진 선택")
            .showMultiImage(object : TedBottomSheetDialogFragment.OnMultiImageSelectedListener {
                override fun onImagesSelected(uriList: MutableList<Uri>) {
                    val list = arrayListOf<String>()

                    for (uri in uriList) {
                        list.add(uri.toString())
                    }

                    postViewModel.getPostCreateImage(list)
                }
            })
    }


}