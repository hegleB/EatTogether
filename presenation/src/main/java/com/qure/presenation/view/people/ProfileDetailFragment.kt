package com.qure.presenation.view.people


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.quer.presenation.base.BaseFragment
import com.qure.domain.utils.Resource
import com.qure.presenation.CaptureActivity
import com.qure.presenation.R
import com.qure.presenation.databinding.FragmentProfileDetailBinding
import com.qure.presenation.viewmodel.PeopleViewModel
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedbottompicker.TedBottomPicker
import gun0912.tedbottompicker.TedBottomSheetDialogFragment

@AndroidEntryPoint
class ProfileDetailFragment :
    BaseFragment<FragmentProfileDetailBinding>(R.layout.fragment_profile_detail) {

    private val peopleViewModel: PeopleViewModel by activityViewModels()
    private val args: ProfileDetailFragmentArgs by navArgs()
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
        currentUid = peopleViewModel.getCurrentUser()?.uid?:""
        binding.circleImageViewActivityprofileProfile.isEnabled = false
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
            it.consume()
        }

        peopleViewModel.profileCancel.observe(this) {
            if (it.consumed) return@observe
            cancleEditProfile()
            peopleViewModel.getUserInfo(args.uid)
            it.consume()
        }

        peopleViewModel.profileEdit.observe(this) {
            if (it.consumed) return@observe
            editProfile()
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
            peopleViewModel.profileEdit(binding.textViewActivityprofileName.text.toString())
            it.consume()
        }

        peopleViewModel.profileMessageEdit.observe(this) {
            if (it.consumed) return@observe
            ProfileEditDialogFragment().show(
                fragmentManager!!, "message"
            )
            peopleViewModel.profileTag("message")
            peopleViewModel.profileEdit(binding.textViewActivityprofileMessage.text.toString())
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

            toolBarActivityprofile.visibility = View.VISIBLE
            imageViewActivityprofileChangeImage.visibility = View.VISIBLE
            circleImageViewActivityprofileProfile.isEnabled = true
            textViewActivityprofileName.isEnabled = true
            textViewActivityprofileName.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.shape_bottom_border2
                )
            textViewActivityprofileMessage.isEnabled = true
            textViewActivityprofileMessage.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.shape_bottom_border2
                )
            imageViewActivityprofileProfileEdit.visibility = View.INVISIBLE
            textViewActivityprofileCancel.visibility = View.VISIBLE
            textViewActivityprofileSubmit.visibility = View.VISIBLE
            imageViewActivityprofileClose.visibility = View.INVISIBLE
            imageViewActivityprofileQrcode.visibility = View.INVISIBLE
            imageViewActivityprofileScanner.visibility = View.INVISIBLE
        }
    }

    fun cancleEditProfile() {
        binding.apply {

            toolBarActivityprofile.visibility = View.INVISIBLE
            imageViewActivityprofileQrcode.visibility = View.INVISIBLE
            imageViewActivityprofileScanner.visibility = View.INVISIBLE
            imageViewActivityprofileChangeImage.visibility = View.INVISIBLE
            circleImageViewActivityprofileProfile.isEnabled = false
            textViewActivityprofileName.isEnabled = false
            textViewActivityprofileName.background =
                ContextCompat.getDrawable(requireContext(), R.color.white)
            textViewActivityprofileMessage.isEnabled = false
            textViewActivityprofileMessage.background =
                ContextCompat.getDrawable(requireContext(), R.color.white)
            peopleViewModel.checkBarcode.observe(viewLifecycleOwner) {
                if (!it) {
                    imageViewActivityprofileProfileEdit.visibility = View.VISIBLE
                    imageViewActivityprofileQrcode.visibility = View.VISIBLE
                    imageViewActivityprofileScanner.visibility = View.VISIBLE
                } else {
                    imageViewActivityprofileProfileEdit.visibility = View.VISIBLE
                    imageViewActivityprofileQrcode.visibility = View.GONE
                    imageViewActivityprofileScanner.visibility = View.GONE
                }
            }
            textViewActivityprofileCancel.visibility = View.INVISIBLE
            textViewActivityprofileSubmit.visibility = View.INVISIBLE
            imageViewActivityprofileClose.visibility = View.VISIBLE
        }
    }

    private fun checkOtherProfile() {
        peopleViewModel.otherProfile.observe(viewLifecycleOwner) {
            Log.d("OtherProfile", "${it}")
            binding.apply {
            if (it.equals(false)) {
                    imageViewActivityprofileProfileEdit.visibility = View.GONE
                    imageViewActivityprofileQrcode.visibility = View.GONE
                    imageViewActivityprofileScanner.visibility = View.GONE
                    textViewActivityprofileBarcodeTime.visibility = View.GONE

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
        val result = IntentIntegrator.parseActivityResult(requestCode,resultCode, data)
        if (result!=null) {
            Log.d("QRcode", "${result.contents}")
            if (result.contents != null) {

                peopleViewModel.apply {
                    val currentUid = getCurrentUser()?.uid ?: ""
                    Snackbar.make(
                        binding.constraintLayoutActivityprofile,
                        "확인 되었습니다.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    setBarcodeTime()
                    getMeetingCount(currentUid)
                    countTime()
                    updateMeetingCount()
                }
            }
        } else {
            Log.d("QRcode", "null")
        }

    }

    private fun countTime() {
        peopleViewModel.getBarcodeTime()

        binding.imageViewActivityprofileScanner.visibility = View.GONE
        binding.imageViewActivityprofileQrcode.visibility = View.GONE
        binding.textViewActivityprofileBarcodeTime.visibility = View.VISIBLE

        checkOtherProfile()

        var countDownTimer: CountDownTimer? = null
        peopleViewModel.barcodeTimeRemaining.observe(this) {
            countDownTimer = object : CountDownTimer(it, 1000) {
                override fun onTick(l: Long) {
                    peopleViewModel.countBarcodeTime(l)
                }

                override fun onFinish() {
                    binding.imageViewActivityprofileScanner.visibility = View.VISIBLE
                    binding.imageViewActivityprofileQrcode.visibility = View.VISIBLE
                    binding.textViewActivityprofileBarcodeTime.visibility = View.GONE
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

            override fun onPermissionDenied(deniedPermissions: java.util.ArrayList<String>?) {
                Snackbar.make(
                    binding.constraintLayoutActivityprofile, "Permission Denied\n" +
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
                        peopleViewModel.changeImage(uriList.get(0))
                    }
                }
            })
    }

    private fun updateProfile() {
        peopleViewModel.uploadImage()

        peopleViewModel.updatedState.observe(this) {
            when (it) {
                is Resource.Success -> {
                    binding.spinKitViweActivityprofileLoading.visibility = View.GONE
                    peopleViewModel.chageProfile()
                    findNavController().popBackStack()
                }
                is Resource.Loading -> {
                    binding.spinKitViweActivityprofileLoading.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.spinKitViweActivityprofileLoading.visibility =
                        View.GONE
                }
            }
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
}