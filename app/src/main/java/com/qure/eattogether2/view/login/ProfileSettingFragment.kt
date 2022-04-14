package com.qure.eattogether2.view.login

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.qure.eattogether2.R
import com.qure.eattogether2.data.BarcodeScan
import com.qure.eattogether2.data.Setting
import com.qure.eattogether2.data.User
import com.qure.eattogether2.databinding.FragmentProfileSettingBinding
import com.qure.eattogether2.view.home.HomeActivity
import com.qure.eattogether2.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedbottompicker.TedBottomPicker
import gun0912.tedbottompicker.TedBottomSheetDialogFragment
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class ProfileSettingFragment : Fragment() {


    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseStorage: FirebaseStorage

    @Inject
    lateinit var firebaseAuth: FirebaseAuth


    private val viewModel by viewModels<AuthViewModel>()
    private lateinit var binding: FragmentProfileSettingBinding
    private var isChageImage = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile_setting,
            container,
            false
        )

        val uid = firebaseAuth.currentUser?.uid
        val userid = firebaseAuth.currentUser?.email


        binding.profileSettingSubmit.setOnClickListener {

            binding.profileSettingLoading.visibility = View.VISIBLE

            val name = binding.profileSettingName.text.toString()
            if (name.equals("")) {
                Toast.makeText(requireContext(), "이름을 입력해주세요.", Toast.LENGTH_LONG).show()
            } else {
                val msg = binding.profileSettingMessage.text.toString()

                if (isChageImage) {
                    viewModel.profileUri.observe(viewLifecycleOwner, {

                        if (it != null) {
                            val riverRef: StorageReference =
                                firebaseStorage.getReference()
                                    .child("profile_image/" + uid + ".jpg")


                            val uploadTask: UploadTask = riverRef.putFile(it)

                            uploadTask.addOnSuccessListener {

                                riverRef.downloadUrl.addOnSuccessListener { uri ->
                                    firebaseID(uri.toString(), name, msg, uid!!, userid!!)
                                    binding.profileSettingLoading.visibility = View.GONE
                                    moveToHome()

                                }
                            }
                        }

                    })
                } else {


                    firebaseID("", name, msg, uid!!, userid!!)
                    binding.profileSettingLoading.visibility = View.GONE
                    moveToHome()

                }

            }
        }



        binding.profileSettingImage.setOnClickListener {

            requestPermission(uid!!)

        }


        return binding.root
    }


    fun moveToHome() {
        activity?.let {
            val intent = Intent(context, HomeActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    fun firebaseID(
        userphoto: String,
        username: String,
        usermessage: String,
        uid: String,
        userid: String
    ) {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result

            val msg = token.toString()

            val user = User(userid!!, uid!!, username, msg, userphoto, usermessage)

            val now = System.currentTimeMillis()
            firestore.collection("setting").document(uid!!).set(Setting(true, true,true, now))

            firestore.collection("users")
                .document(uid!!).set(user, SetOptions.merge())

            firestore.collection("meeting").document(uid!!).set(BarcodeScan(0))

        })

    }

    private fun requestPermission(uid: String) {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                openImagesPicker(uid!!)
            }

            override fun onPermissionDenied(deniedPermissions: java.util.ArrayList<String>?) {
                Toast.makeText(
                    requireContext(),
                    "Permission Denied\n" + deniedPermissions.toString(),
                    Toast.LENGTH_SHORT
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

    private fun setProfileImage(uri: Uri) {
        isChageImage = true
        viewModel.profileUri.value = uri
        Glide.with(requireContext()).load(uri).into(binding.profileSettingImage)

    }

    private fun openImagesPicker(currentUid: String) {

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

                        setProfileImage(uriList.get(0))
                    }

                }
            })
    }

}