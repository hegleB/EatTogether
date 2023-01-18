package com.qure.presenation.view.people


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.zxing.integration.android.IntentIntegrator
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.quer.presenation.base.BaseFragment
import com.qure.domain.utils.Resource
import com.qure.presenation.CaptureActivity
import com.qure.presenation.R
import com.qure.presenation.adapter.ProfileViewPagerAdapter
import com.qure.presenation.databinding.FragmentProfileDetailBinding
import com.qure.presenation.utils.BottomImagePicker
import com.qure.presenation.viewmodel.PeopleViewModel
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedbottompicker.TedBottomPicker
import gun0912.tedbottompicker.TedBottomSheetDialogFragment

@AndroidEntryPoint
class ProfileDetailFragment :
    BaseFragment<FragmentProfileDetailBinding>(R.layout.fragment_profile_detail) {

    private val peopleViewModel: PeopleViewModel by activityViewModels()
    private val args: ProfileDetailFragmentArgs by navArgs()
    private val bottomImagePicker by lazy {
        BottomImagePicker(requireContext(), requireActivity())
    }
    private var currentUid = ""

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
            cancleEditProfile()
            peopleViewModel.setClosedProfileState()
            it.consume()
        }

        peopleViewModel.profileCancel.observe(this) {
            if (it.consumed) return@observe
            cancleEditProfile()
            peopleViewModel.getUserInfo(args.uid)
            peopleViewModel.setClosedProfileState()
            peopleViewModel.setQRAbledState()
            it.consume()
        }

        peopleViewModel.profileEdit.observe(this) {
            if (it.consumed) return@observe
            editProfile()
            peopleViewModel.setEditedProfileState()
            peopleViewModel.setQRDisabledState()
            it.consume()
        }

        peopleViewModel.profileQRCode.observe(this) {
            if (it.consumed) return@observe
            BarcodeDiaglogFragment().show(
                fragmentManager!!, "BarcodeDialogFragment"
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
            ProfileEditDialogFragment().show(
                fragmentManager!!, "name"
            )
            peopleViewModel.profileTag("name")
            peopleViewModel.profileEdit(binding.textViewFragmentProfileName.text.toString())
            it.consume()
        }

        peopleViewModel.profileMessageEdit.observe(this) {
            if (it.consumed) return@observe
            ProfileEditDialogFragment().show(
                fragmentManager!!, "message"
            )
            peopleViewModel.profileTag("message")
            peopleViewModel.profileEdit(binding.textViewFragmentProfileMessage.text.toString())
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


    private fun editProfile() {
        binding.apply {
            circleImageViewFragmentProfileProfile.isEnabled = true
            textViewFragmentProfileName.isEnabled = true
            textViewFragmentProfileName.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.shape_bottom_border2
                )
            textViewFragmentProfileMessage.isEnabled = true
            textViewFragmentProfileMessage.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.shape_bottom_border2
                )
        }
    }

    fun cancleEditProfile() {
        binding.apply {
            circleImageViewFragmentProfileProfile.isEnabled = false
            textViewFragmentProfileName.isEnabled = false
            textViewFragmentProfileName.background =
                ContextCompat.getDrawable(requireContext(), R.color.white)
            textViewFragmentProfileMessage.isEnabled = false
            textViewFragmentProfileMessage.background =
                ContextCompat.getDrawable(requireContext(), R.color.white)
            peopleViewModel.checkBarcode.observe(viewLifecycleOwner) {
                if (!it) {
                    peopleViewModel.setClosedProfileState()
                } else {
                    peopleViewModel.setEditedProfileState()
                }
            }
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

        val integrator = IntentIntegrator.forSupportFragment(this@ProfileDetailFragment)
        integrator.setCaptureActivity(CaptureActivity::class.java)
        integrator.setOrientationLocked(true)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setBarcodeImageEnabled(false)
        integrator.setPrompt("스캔 중...")
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {

                peopleViewModel.apply {
                    val currentUid = getCurrentUser()?.uid ?: ""
                    Snackbar.make(
                        binding.constraintLayoutFragmentProfile,
                        "확인 되었습니다.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    setBarcodeTime()
                    getMeetingCount(currentUid)
                    countTime()
                    updateMeetingCount()
                }
            }
        }
    }

    private fun countTime() {
        peopleViewModel.getBarcodeTime()
        checkOtherProfile()

        var countDownTimer: CountDownTimer? = null
        peopleViewModel.barcodeTimeRemaining.observe(this) {
            peopleViewModel.setQRAbledState()
            countDownTimer = object : CountDownTimer(it, 1000) {
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
                bottomImagePicker.openImagePicker("1개만 선택이 가능합니다.", "선택")
                    .showMultiImage { uriList ->
                        if (uriList!!.size > 0) {
                            peopleViewModel.changeImage(uriList.get(0))
                        }
                    }
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                bottomImagePicker.showSnackBarMessage(
                    binding.constraintLayoutFragmentProfile,
                    deniedPermissions ?: arrayListOf()
                )
            }
        }
       bottomImagePicker.setPermission(permissionListener)
    }

    private fun updateProfile() {
        peopleViewModel.uploadImage()

        peopleViewModel.updatedState.observe(this) {
            when (it) {
                is Resource.Success -> {
                    peopleViewModel.chageProfile()
                    findNavController().popBackStack()
                }
                is Resource.Error -> {
                    binding.spinKitViweFragmentProfileLoading.visibility =
                        View.GONE
                }
            }
        }
    }

    private fun initViewPager() {
        val profileViewPagerAdapter: ProfileViewPagerAdapter by lazy {
            ProfileViewPagerAdapter(childFragmentManager, lifecycle, args.uid)
        }
        val tabTitles = listOf("작성한 글", "좋아한 글", "댓글단 글")
        binding.viewPagerFragmentProfile.adapter = profileViewPagerAdapter
        TabLayoutMediator(
            binding.tabLayoutFragmentProfile,
            binding.viewPagerFragmentProfile,
            { tab, position -> tab.text = tabTitles[position] }
        ).attach()
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
}