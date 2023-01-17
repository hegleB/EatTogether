package com.qure.presenation.view.login

import android.net.Uri
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.quer.presenation.base.BaseFragment
import com.qure.domain.utils.Resource
import com.qure.presenation.R
import com.qure.presenation.databinding.FragmentProfileSettingBinding
import com.qure.presenation.utils.OnBackPressedListener
import com.qure.presenation.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedbottompicker.TedBottomPicker
import gun0912.tedbottompicker.TedBottomSheetDialogFragment

@AndroidEntryPoint
class ProfileSettingFragment :
    BaseFragment<FragmentProfileSettingBinding>(R.layout.fragment_profile_setting) {

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun init() {
        initViewModel()
        observeViewModel()
        OnBackPressedListener().back(requireActivity(), findNavController())
    }

    private fun initViewModel() {
        binding.viewmodel = authViewModel

        authViewModel.apply {
            getCurrentUser()
            getMessageToken()
        }
    }

    private fun observeViewModel() {
        authViewModel.buttonSettingSubmit.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            if (checkProfileName()) {
                Snackbar.make(requireView(), "이름을 입력하세요.", Snackbar.LENGTH_SHORT).show()
            } else {
                storageUser()
            }
            it.consume()
        }

        authViewModel.buttonUserImage.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            requestPermission()
            it.consume()
        }
    }

    private fun moveToHome() {
        findNavController().navigate(R.id.action_loginBottomSheetDialog_to_profileSettingFragment)
    }

    private fun checkProfileName(): Boolean {
        val name = binding.editTextFragmentProfileSettingName.text.toString()
        return name.equals("")
    }

    private fun requestPermission() {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                openImagePicker()
            }

            override fun onPermissionDenied(deniedPermissions: java.util.ArrayList<String>?) {
                Snackbar.make(
                    requireView(), "Permission Denied\n" +
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
            .setSelectMaxCount(1)
            .setSelectMaxCountErrorText("1개만 선택이 가능합니다.")
            .showTitle(false)
            .setTitleBackgroundResId(R.color.light_red)
            .setGalleryTileBackgroundResId(R.color.white)
            .setCompleteButtonText("선택")
            .setEmptySelectionText("사진 선택")
            .showMultiImage(object : TedBottomSheetDialogFragment.OnMultiImageSelectedListener {
                override fun onImagesSelected(uriList: MutableList<Uri>?) {
                    if (uriList!!.size > 0) {
                        authViewModel.getImageUri(uriList.get(0))
                    }
                }
            })
    }

    private fun storageUser() {
        authViewModel.storageProfile()

        authViewModel.settingState.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    addFirebaseStore()
                    moveToHome()

                }
                is Resource.Error -> {
                    Snackbar.make(requireView(), "이미지 업로드 실패", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun addFirebaseStore() {
        val now = System.currentTimeMillis()
        authViewModel.setFireStoreUser(now)
    }
}