package com.qure.presenation.view.people

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.zxing.integration.android.IntentIntegrator
import com.gun0912.tedpermission.PermissionListener
import com.quer.presenation.base.BaseFragment
import com.qure.domain.utils.Resource
import com.qure.presenation.CaptureActivity
import com.qure.presenation.R
import com.qure.presenation.adapter.ProfileViewPagerAdapter
import com.qure.presenation.databinding.FragmentProfileDetailBinding
import com.qure.presenation.utils.BottomImagePicker
import com.qure.presenation.utils.SnackBar
import com.qure.presenation.viewmodel.PeopleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDetailFragment :
    BaseFragment<FragmentProfileDetailBinding>(R.layout.fragment_profile_detail) {

    private val peopleViewModel: PeopleViewModel by activityViewModels()
    private val args: ProfileDetailFragmentArgs by navArgs()
    private val bottomImagePicker by lazy {
        BottomImagePicker(requireContext(), requireActivity())
    }
    private var countDownTimer: CountDownTimer? = null
    private var currentUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navBar = activity!!.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navBar.visibility = View.GONE
    }

    override fun init() {
        initViewModel()
        observeViewModel()
        onBackPressedEvent()
        checkOtherProfile()
        currentUid = peopleViewModel.getCurrentUser()?.uid ?: ""
        binding.circleImageViewFragmentProfileProfile.isEnabled = false
    }

    override fun onResume() {
        super.onResume()
        initViewPager()
    }

    private fun initViewModel() {
        binding.viewmodel = peopleViewModel
        peopleViewModel.apply {
            getLikeCount(args.uid)
            getMeetingCount(args.uid)
            getPostCount(args.uid)
            checkCurrentUser(args.uid)
            getUserInfo(args.uid)
            checkBarcodeTime()
        }
    }

    private fun observeViewModel() {
        peopleViewModel.profileClose.observe(this) {
            if (it.consumed) return@observe
            peopleViewModel.setQRAbledState()
            if (args.uid.equals(currentUid)) {
                updateProfile()
            } else {
                findNavController().popBackStack()
            }
            it.consume()
        }

        peopleViewModel.profileSubmit.observe(this) {
            if (it.consumed) return@observe
            setEditProfileView()
            peopleViewModel.setClosedProfileState()
            it.consume()
        }

        peopleViewModel.profileCancel.observe(this) {
            if (it.consumed) return@observe
            setEditProfileView()
            with(peopleViewModel) {
                getUserInfo(args.uid)
                setClosedProfileState()
                setQRAbledState()
            }
            it.consume()
        }

        peopleViewModel.profileEdit.observe(this) {
            if (it.consumed) return@observe
            setEditProfileView()
            with(peopleViewModel) {
                setEditedProfileState()
                setQRDisabledState()
            }
            it.consume()
        }

        peopleViewModel.profileQRCode.observe(this) {
            if (it.consumed) return@observe
            BarcodeDiaglogFragment().show(
                fragmentManager!!,
                "BarcodeDialogFragment",
            )
            it.consume()
        }

        peopleViewModel.profileQRScanner.observe(this) {
            if (it.consumed) return@observe
            scanBarcode()
            it.consume()
        }

        peopleViewModel.profileNameEdit.observe(this) {
            if (it.consumed) return@observe
            ProfileEditDialogFragment().show(fragmentManager!!, NAME_TAG)
            with(peopleViewModel) {
                profileTag(NAME_TAG)
                profileEdit(binding.textViewFragmentProfileName.text.toString())
            }
            it.consume()
        }

        peopleViewModel.profileMessageEdit.observe(this) {
            if (it.consumed) return@observe
            ProfileEditDialogFragment().show(
                fragmentManager!!,
                MESSAGE_TAG,
            )
            with(peopleViewModel) {
                profileTag(MESSAGE_TAG)
                profileEdit(binding.textViewFragmentProfileMessage.text.toString())
            }
            it.consume()
        }

        peopleViewModel.profileImageEdit.observe(this) {
            if (it.consumed) return@observe
            requestPermission()
            it.consume()
        }

        peopleViewModel.checkBarcode.observe(this) {
            if (it) {
                countTime()
            }
        }
        peopleViewModel.profileEditText.observe(this) {
            peopleViewModel.updateEditText()
        }
    }

    private fun setEditProfileView() {
        binding.apply {
            val isEnabled = circleImageViewFragmentProfileProfile.isEnabled
            circleImageViewFragmentProfileProfile.isEnabled = if (isEnabled) false else true
            textViewFragmentProfileName.isEnabled = if (isEnabled) false else true
            textViewFragmentProfileMessage.isEnabled = if (isEnabled) false else true
            textViewFragmentProfileName.background = getProfileNameDrawalbe(isEnabled)
            textViewFragmentProfileMessage.background = getProfileNameDrawalbe(isEnabled)
        }
    }

    private fun getProfileNameDrawalbe(isEnabled: Boolean): Drawable? {
        return when (isEnabled) {
            false -> ContextCompat.getDrawable(requireContext(), R.drawable.shape_bottom_border2)
            else -> ContextCompat.getDrawable(requireContext(), R.color.white)
        }
    }

    private fun checkOtherProfile() {
        peopleViewModel.otherProfile.observe(viewLifecycleOwner) {
            binding.apply {
                if (it.equals(false)) {
                    peopleViewModel.setQRDisabledState()
                }
            }
        }
    }

    private fun scanBarcode() {
        with(IntentIntegrator.forSupportFragment(this@ProfileDetailFragment)) {
            setCaptureActivity(CaptureActivity::class.java)
            setOrientationLocked(true)
            setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            setBarcodeImageEnabled(false)
            setPrompt(PROMPT_TEXT)
            initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result.contents != null) {
            SnackBar.show(binding.constraintLayoutFragmentProfile, QR_SCAN_SNACKBAR_MESSAGE)
            peopleViewModel.apply {
                val currentUid = getCurrentUser()?.uid ?: ""
                setBarcodeTime()
                getMeetingCount(currentUid)
                countTime()
                updateMeetingCount()
            }
        }
    }

    private fun countTime() {
        peopleViewModel.getBarcodeTime()
        checkOtherProfile()
        peopleViewModel.barcodeTimeRemaining.observe(this) {
            peopleViewModel.setQRAbledState()
            countDownTimer = object : CountDownTimer(it, COUNTDOWN_INTERVAL) {
                override fun onTick(l: Long) {
                    peopleViewModel.countBarcodeTime(l)
                }

                override fun onFinish() {
                    peopleViewModel.setQRDisabledState()
                    peopleViewModel.deleteBarcodeTime()
                }
            }.start()
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

    private fun openImagePicker() {
        bottomImagePicker.openImagePicker("1개만 선택이 가능합니다.", "선택")
            .showMultiImage { uriList ->
                if (uriList!!.size > 0) {
                    peopleViewModel.changeImage(uriList.get(0))
                }
            }
    }

    private fun showPermissionSnackBar(deniedPermissions: ArrayList<String>?) {
        bottomImagePicker.showSnackBarMessage(requireView(), deniedPermissions ?: arrayListOf())
    }

    private fun updateProfile() {
        peopleViewModel.uploadImage()
        peopleViewModel.updatedState.observe(this) {
            when (it) {
                is Resource.Success -> {
                    peopleViewModel.chageProfile()
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun initViewPager() {
        val tabTitles = listOf("작성한 글", "좋아한 글", "댓글단 글")
        val profileViewPagerAdapter: ProfileViewPagerAdapter by lazy {
            ProfileViewPagerAdapter(childFragmentManager, lifecycle, args.uid)
        }
        binding.apply {
            viewPagerFragmentProfile.adapter = profileViewPagerAdapter
            TabLayoutMediator(
                tabLayoutFragmentProfile,
                viewPagerFragmentProfile,
                { tab, position -> tab.text = tabTitles[position] },
            ).attach()
        }
    }

    private fun onBackPressedEvent() {
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (args.uid.equals(currentUid)) {
                    updateProfile()
                } else {
                    findNavController().popBackStack()
                }
            }
        })
    }

    companion object {
        const val NAME_TAG = "name"
        const val MESSAGE_TAG = "message"
        const val PROMPT_TEXT = "스캔 중..."
        const val QR_SCAN_SNACKBAR_MESSAGE = "확인 되었습니다."
        const val COUNTDOWN_INTERVAL = 1000L
    }
}
