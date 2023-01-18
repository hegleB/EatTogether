package com.qure.presenation.view.login

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.gun0912.tedpermission.PermissionListener
import com.quer.presenation.base.BaseFragment
import com.qure.domain.utils.Resource
import com.qure.presenation.R
import com.qure.presenation.databinding.FragmentProfileSettingBinding
import com.qure.presenation.utils.BottomImagePicker
import com.qure.presenation.utils.OnBackPressedListener
import com.qure.presenation.utils.SnackBar
import com.qure.presenation.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileSettingFragment :
    BaseFragment<FragmentProfileSettingBinding>(R.layout.fragment_profile_setting) {

    private val authViewModel: AuthViewModel by activityViewModels()
    private val bottomImagePicker by lazy {
        BottomImagePicker(requireContext(), requireActivity())
    }

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
            when {
                checkProfileName() -> SnackBar.show(requireView(), PROFILE_NAME_EMPTY_MESSAGE)
                else -> storageUser()
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
        findNavController().navigate(R.id.action_profileSettingFragment_to_peopleFragment)
    }

    private fun checkProfileName(): Boolean {
        val name = binding.editTextFragmentProfileSettingName.text.toString()
        return name.length == 0
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
        bottomImagePicker.showSnackBarMessage(requireView(), deniedPermissions ?: arrayListOf())
    }

    private fun openImagePicker() {
        bottomImagePicker.openImagePicker("1개만 선택이 가능합니다.", "선택")
            .showMultiImage { uriList ->
                if (uriList!!.size > 0) {
                    authViewModel.getImageUri(uriList.get(0))
                }
            }
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
                    SnackBar.show(requireView(), IMAGE_UPLOAD_FAIL_MESSAGE)
                }
            }
        }
    }

    private fun addFirebaseStore() {
        val now = System.currentTimeMillis()
        authViewModel.setFireStoreUser(now)
    }

    companion object {
        const val PROFILE_NAME_EMPTY_MESSAGE = "이름을 입력하세요."
        const val IMAGE_UPLOAD_FAIL_MESSAGE = "이미지 업로드 실패"
    }
}
