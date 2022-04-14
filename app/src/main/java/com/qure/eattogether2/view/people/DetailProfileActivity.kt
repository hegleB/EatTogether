package com.qure.eattogether2.view.people

import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.integration.android.IntentIntegrator
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.qure.eattogether2.R
import com.qure.eattogether2.adapter.ProfileViewPagerAdapter
import com.qure.eattogether2.bindingadapter.ImageBindingAdapter
import com.qure.eattogether2.data.*
import com.qure.eattogether2.databinding.ActivityDetailProfileBinding
import com.qure.eattogether2.view.home.HomeActivity
import com.qure.eattogether2.viewmodel.ProfileViewModel
import com.yy.mobile.rollingtextview.CharOrder
import com.yy.mobile.rollingtextview.RollingTextView
import com.yy.mobile.rollingtextview.strategy.Direction
import com.yy.mobile.rollingtextview.strategy.Strategy
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedbottompicker.TedBottomPicker
import gun0912.tedbottompicker.TedBottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_detail_profile.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class DetailProfileActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseStorage: FirebaseStorage

    private lateinit var binding: ActivityDetailProfileBinding

    private lateinit var profileviewModel: ProfileViewModel
    private var countDownTimer: CountDownTimer? = null
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val args by navArgs<DetailProfileActivityArgs>()
    private var isOtherPorifile = false
    private var isScan = false
    private var isChange = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_profile)



        profileviewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        profileviewModel.isEdit.value = false



        pref = getPreferences(Context.MODE_PRIVATE)
        editor = pref.edit()


        val editData = intent.getStringExtra("editData")
        val user = intent.getParcelableExtra<User>("user")
//        val editData = args.editData
//        val user = args.user

        val uid = firebaseAuth.currentUser!!.uid

        var profileImage = pref.getString("profileImage", "")
        var profileName = pref.getString("profileName", "")
        var profileMessage = pref.getString("profileMessage", "")


        binding.apply {


            if (!firebaseAuth.currentUser!!.uid.equals(user!!.uid)) {
                detailProfileEdit.visibility = View.INVISIBLE
                isOtherPorifile = true
            }
            ImageBindingAdapter.userImage(detailProfileImage, profileviewModel.profileImage.value)

            detailProfileClose.setOnClickListener {

                binding.detailProfileLoading.visibility = View.VISIBLE


                uploadImage(
                    user,
                    detailProfileName.text.toString(),
                    detailProfileMessage.text.toString()
                )



                editor.putBoolean("changeImage", false)
                finishAffinity()
            }



            profileviewModel.isEdit.observe(this@DetailProfileActivity, {
                if (it) {
                    EditProfile()
                    countTime(uid)
                } else {
                    cancleEditProfile()

                    countTime(uid)
                }
            })



            profileviewModel.isEdit.observe(this@DetailProfileActivity, {
                if (it) {
                    detailProfileImage.setOnClickListener {
                        requestPermission()
                    }
                }
            })


            getMyProfile(user, editData!!, profileName!!, profileImage!!, profileMessage!!)
            getProfileCountInfo(user)



            profileviewModel.user.observe(this@DetailProfileActivity, {
                binding.user = it

            })





            detailProfileEdit.setOnClickListener {
                profileviewModel.isEdit.value = true
                EditProfile()
            }

            detailProfileCancel.setOnClickListener {

                profileviewModel.isEdit.value = false
                cancleEditProfile()

            }
            detailProfileSubmit.setOnClickListener {


                profileviewModel.isEdit.value = false
                cancleEditProfile()

            }




            detailProfileName.setOnClickListener {


                dialog(detailProfileName, "name")


            }

            detailProfileMessage.setOnClickListener {


                dialog(detailProfileMessage, "message")

            }

            detailProfileQrcode.setOnClickListener {

                Barcodedialog()

            }


            detailProfileQrcodeScanner.setOnClickListener {
                scanBarcode()
            }


            val tabTitles = listOf<String>("작성한 글", "좋아한 글", "댓글단 글")

            val fragmentManager = supportFragmentManager



            detailProfileViewpager.adapter =
                ProfileViewPagerAdapter(fragmentManager, lifecycle, user)
            TabLayoutMediator(
                detailProfileTablayout,
                detailProfileViewpager,
                { tab, position -> tab.text = tabTitles[position] }).attach()


        }
        if(user!!.uid.equals(uid)) {
            countTime(uid)
        }
    }



    fun getMyProfile(
        user: User,
        editData: String,
        profileName: String,
        profileImage: String,
        profileMessage: String
    ) {

        firestore.collection("users").document(user!!.uid)
            .addSnapshotListener { snapshot, e ->
                val data = snapshot!!.toObject(User::class.java)

                if (editData.equals("")) {
                    binding.user = data
                } else {

                    profileviewModel.isEdit.value = true
                    profileviewModel.profileImage.value = data!!.userphoto
                    profileviewModel.user.value =
                        User(
                            data!!.userid,
                            data!!.uid,
                            profileName!!,
                            data!!.token,
                            profileImage!!,
                            profileMessage!!
                        )
                }
            }
    }


    private fun startCountAnimation(rollingTextView: RollingTextView, count: String) {

        val random = Random()
        val r = random.nextInt(1000)
        val animator = ValueAnimator.ofInt(1000, 0)
        rollingTextView.animationDuration = 1000L
        rollingTextView.charStrategy = Strategy.CarryBitAnimation(Direction.SCROLL_UP)
        rollingTextView.addCharOrder(CharOrder.Number)
        rollingTextView.animationInterpolator =
            AccelerateDecelerateInterpolator()//0 is min number, 600 is max number
        animator.animatedFraction
        animator.addUpdateListener { animation -> rollingTextView.setText(animation.animatedValue.toString()) }
        animator.start()
        val handler = Handler()
        handler.postDelayed({
            animator.cancel()
            rollingTextView.setText(count)
        }, 0)


    }

    fun EditProfile() {

        binding.apply {

            detailProfileToolbar.visibility = View.VISIBLE
            detailProfileEditImage.visibility = View.VISIBLE
            detailProfileImage.isEnabled = true
            detailProfileName.isEnabled = true
            detailProfileName.background =
                ContextCompat.getDrawable(
                    this@DetailProfileActivity,
                    R.drawable.shape_bottom_border2
                )
            detailProfileMessage.isEnabled = true
            detailProfileMessage.background =
                ContextCompat.getDrawable(
                    this@DetailProfileActivity,
                    R.drawable.shape_bottom_border2
                )
            detailProfileEdit.visibility = View.INVISIBLE
            detailProfileCancel.visibility = View.VISIBLE
            detailProfileSubmit.visibility = View.VISIBLE
            detailProfileClose.visibility = View.INVISIBLE
            detailProfileQrcode.visibility = View.INVISIBLE
            detailProfileQrcodeScanner.visibility = View.INVISIBLE


        }
    }

    fun getProfileCountInfo(user: User) {
        binding.apply {
            firestore.collection("posts").whereEqualTo("uid", user.uid).get()
                .addOnCompleteListener { snapshot ->
                    if (snapshot.isSuccessful) {
                        val postCount = snapshot.result.toObjects(Post::class.java)
                        startCountAnimation(detailProfilePostCount, postCount.size.toString())
                    }
                }

            firestore.collection("posts").whereArrayContains("likecount", user!!.uid).get()
                .addOnCompleteListener { snapshot ->
                    if (snapshot.isSuccessful) {
                        val likeCount = snapshot.result.toObjects(Post::class.java)
                        startCountAnimation(detailProfileLikeCount, likeCount.size.toString())
                    }
                }

            firestore.collection("meeting").document(user!!.uid).addSnapshotListener(){ snaphot,e ->
                    if (snaphot!!.exists()) {
                        val meetingCount = snaphot.toObject(BarcodeScan::class.java)
                        startCountAnimation(
                            detailProfileMeetingCount,
                            meetingCount!!.meeting.toString()
                        )

                    }
                }
        }

    }

    fun uploadImage(user: User, name: String, msg: String) {
        val riverRef: StorageReference =
            firebaseStorage.getReference()
                .child("profile_image/" + user.uid + ".jpg")
        var profileImage = pref.getString("profileImage", "")


        val imageUri = Uri.parse(profileImage)

        val uploadTask: UploadTask = riverRef.putFile(imageUri)
        if (isChange) {

            uploadTask.addOnSuccessListener {

                riverRef.downloadUrl.addOnSuccessListener { uri ->


                    editor.putBoolean("changeImage", true)
                    editor.apply()

                }
            }


            uploadTask.addOnProgressListener {
                val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                if (progress == 100.0) {
                    binding.detailProfileLoading.visibility = View.GONE


                }
            }

            val urlTask = uploadTask?.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                riverRef.downloadUrl
            }?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firestore.collection("users").document(user.uid)
                        .update("userphoto", task.result.toString())
                    firestore.collection("users").document(user.uid).update("usernm", name)
                    firestore.collection("users").document(user.uid).update("usermsg", msg)
                    finish()

                }
            }
        } else {
            firestore.collection("users").document(user.uid).update("usernm", name)
            firestore.collection("users").document(user.uid).update("usermsg", msg)
            var intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }


    }


    fun cancleEditProfile() {
        binding.apply {

            detailProfileToolbar.visibility = View.INVISIBLE
            detailProfileQrcode.visibility = View.INVISIBLE
            detailProfileQrcodeScanner.visibility = View.INVISIBLE
            detailProfileEditImage.visibility = View.INVISIBLE
            detailProfileImage.isEnabled = false
            detailProfileName.isEnabled = false
            detailProfileName.background =
                ContextCompat.getDrawable(this@DetailProfileActivity, R.color.white)
            detailProfileMessage.isEnabled = false
            detailProfileMessage.background =
                ContextCompat.getDrawable(this@DetailProfileActivity, R.color.white)
            if (!isOtherPorifile) {
                detailProfileEdit.visibility = View.VISIBLE
                detailProfileQrcode.visibility = View.VISIBLE
                detailProfileQrcodeScanner.visibility = View.VISIBLE
            }
            detailProfileCancel.visibility = View.INVISIBLE
            detailProfileSubmit.visibility = View.INVISIBLE
            detailProfileClose.visibility = View.VISIBLE



        }

    }


    override fun onBackPressed() {
        super.onBackPressed()

        var name = ""
        var msg = ""

        var changeImage = pref.getBoolean("changeImage", false)

        val intent = Intent(this, HomeActivity::class.java)



        binding.apply {

            name = detailProfileName.text.toString()
            msg = detailProfileMessage.text.toString()
        }


        profileviewModel.isEdit.observe(this@DetailProfileActivity, { edit ->

            if (edit) {
                cancleEditProfile()
                startActivity(intent)
                countDownTimer!!.cancel()
                finishAffinity()

            } else {
                binding.detailProfileLoading.visibility = View.VISIBLE
                profileviewModel.user.observe(this, {
                    uploadImage(it, name, msg)
                })
                startActivity(intent)
                finishAffinity()

                editor.putBoolean("changeImage", false)


            }
        })
    }

    private fun requestPermission() {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                openImagesPicker()
            }

            override fun onPermissionDenied(deniedPermissions: java.util.ArrayList<String>?) {
                Toast.makeText(
                    this@DetailProfileActivity,
                    "Permission Denied\n" + deniedPermissions.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        TedPermission.with(this)
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

        isChange = true
        editor.putString("profileImage", uri.toString())
        editor.apply()
        Glide.with(this).load(uri).into(binding.detailProfileImage)

    }

    private fun openImagesPicker() {

        TedBottomPicker.with(this)
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
            .showMultiImage(object :
                TedBottomSheetDialogFragment.OnMultiImageSelectedListener {
                override fun onImagesSelected(uriList: MutableList<Uri>?) {

                    if (uriList!!.size > 0) {

                        setProfileImage(uriList.get(0))
                    }

                }
            })
    }

    fun dialog(textView: TextView, type: String) {

        val builder = Dialog(this, R.style.DialogCustomTheme)
        builder.setContentView(R.layout.dialog)

        builder?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        builder.create()
        builder.show()

        val editText = builder.findViewById<TextInputEditText>(R.id.textEdit)
        val editSubmit = builder.findViewById<TextView>(R.id.submit)
        val editCancel = builder.findViewById<TextView>(R.id.cancel)

        editText.setText(textView.text.toString())
        editText.requestFocus()
        editText.setSelection(textView.length())


        editText.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                val DRAWABLE_RIGHT = 2


                if(p1!!.getAction() == MotionEvent.ACTION_UP) {
                    if(p1!!.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        editText.setText("")

                        return true
                    }
                }
                return false
            }

        })


        openKeyboard()
        editSubmit.setOnClickListener {

            hideKeyboard()

            if (type.equals("name")) {

                editor.putString("profileName", editText.text.toString())

            } else {

                editor.putString("profileMessage", editText.text.toString())
            }
            editor.apply()

            textView.setText(editText.text.toString())

            builder.dismiss()
        }

        editCancel.setOnClickListener {
            hideKeyboard()
            builder.dismiss()
        }


    }

    fun Barcodedialog() {

        val builder = Dialog(this, R.style.DialogCustomTheme)
        builder.setContentView(R.layout.barcode_dialog)

        builder?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        builder.create()
        builder.show()

        val barcodeCancel = builder.findViewById<ImageView>(R.id.barcode_close)

        var uid = firebaseAuth.currentUser!!.uid
        var random_num = (Math.random() * 100).toString()
        var random_barcode = uid + random_num;

        val barcodeImage = builder.findViewById<ImageView>(R.id.image_barcode)
        val btnCancel = builder.findViewById<ImageView>(R.id.barcode_close)
        val counter = builder.findViewById<TextView>(R.id.counter)
        val recreate_barcode = builder.findViewById<ImageView>(R.id.recreate_barcode)
        btnCancel.setOnClickListener {
            builder.dismiss()
        }

        firestore.collection("barcode").document(uid).set(Barcode(uid + "_" + random_barcode))

        recreate_barcode.setOnClickListener {
            repeat_barcode(barcodeImage, uid, counter, recreate_barcode)
        }

        recreate_barcode.setOnClickListener {

            val re_random_barcode = uid + "_" + random_barcode

            createQRcode(barcodeImage, re_random_barcode)
            firestore.collection("barcode").document(uid).set(Barcode(re_random_barcode))
            repeat_barcode(barcodeImage, uid, counter, recreate_barcode)
            barcodeImage.visibility = View.VISIBLE
            recreate_barcode.visibility = View.INVISIBLE
        }

        createQRcode(barcodeImage, uid + "_" + random_barcode)
        repeat_barcode(barcodeImage, uid, counter, recreate_barcode)


        barcodeCancel.setOnClickListener {
            firestore.collection("barcode").document(uid).delete()
            builder.dismiss()
        }


    }

    fun createQRcode(img: ImageView, text: String?) {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
            img.setImageBitmap(bitmap)
        } catch (e: Exception) {
        }
    }

    fun repeat_barcode(
        barcode: ImageView?,
        uid: String,
        counter: TextView,
        recreate_barcode: ImageView
    ) {
        var mCountDown: CountDownTimer? = null
        mCountDown = object : CountDownTimer(15000, 1000) {
            override fun onTick(l: Long) {
                val num = (l / 1000).toInt() + 1
                counter.setText(String.format("%02d", num))
            }

            override fun onFinish() {
                counter.setText("00")
                firestore.collection("barcode").document(uid).delete()
                recreate_barcode.visibility = View.VISIBLE

            }
        }.start()
    }

    fun scanBarcode() {


        val integrator = IntentIntegrator(this)
        integrator.setCaptureActivity(CaptureAct::class.java)
        integrator.setOrientationLocked(true)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setBarcodeImageEnabled(false)

        integrator.setPrompt("스캔 중...")
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

            if (result.getContents() == null) {

            } else {
                val scan_txt = result.contents.toString()
                val scan_uid = scan_txt.split("_".toRegex()).toTypedArray()

                firestore.collection("barcode").document(scan_uid.get(0))
                    .get().addOnCompleteListener { snapshot ->

                        if (snapshot!!.isSuccessful) {
                            Toast.makeText(this, "확인 되었습니다.", Toast.LENGTH_SHORT).show()
                            val uid = firebaseAuth.currentUser!!.uid

                            val now = System.currentTimeMillis() + 3 * 3600000
                            firestore.collection("barcode_time").document(uid).set(
                                BarcodeTime(
                                    now
                                )
                            )
                            firestore.collection("meeting").document(uid).get().addOnCompleteListener{
                                meeting ->

                                if(meeting.isSuccessful){
                                    val count = meeting.result.toObject(BarcodeScan::class.java)
                                    val meetingCount = count!!.meeting

                                    firestore.collection("meeting").document(uid).update("meeting", meetingCount.plus(1))
                                }
                            }

                            countTime(uid)

                            binding.detailProfileQrcodeScanner.visibility = View.GONE
                            binding.detailProfileQrcode.visibility = View.GONE
                            binding.detailProfileBarcodeTime.visibility = View.VISIBLE


                        } else {
                            Toast.makeText(this, "바코드를 재 생성해주세요.", Toast.LENGTH_SHORT).show();
                        }

                    }
            }
        }

    }

    fun countTime(uid: String) {
        firestore.collection("barcode_time").document(uid).addSnapshotListener { snapshot, e ->

            if (snapshot!!.exists()) {
                var data = snapshot!!.toObject(BarcodeTime::class.java)
                var CurrentTime = System.currentTimeMillis()

                var remainning_time: Long = data!!.barcodetime - CurrentTime


                if (remainning_time > 0) {

                    binding.detailProfileQrcodeScanner.visibility = View.GONE
                    binding.detailProfileQrcode.visibility = View.GONE
                    binding.detailProfileBarcodeTime.visibility = View.VISIBLE



                    countDownTimer = object : CountDownTimer(remainning_time, 1000) {
                        override fun onTick(l: Long) {
                            val hour = l / 3600000
                            val minues = l % 3600000 / 60000
                            val second = l % 3600000 % 60000 / 1000

                            var scan_time = ""

                            scan_time += hour.toString() + ":"
                            if (minues < 10) scan_time += "0"
                            scan_time += minues.toString() + ":"

                            if (second < 10) scan_time += "0"
                            scan_time += second

                            binding.detailProfileBarcodeTime.setText(scan_time)
                        }

                        override fun onFinish() {
                            binding.detailProfileQrcodeScanner.visibility = View.VISIBLE
                            binding.detailProfileQrcode.visibility = View.VISIBLE
                            binding.detailProfileBarcodeTime.visibility = View.GONE
                        }

                    }.start()

                }

            }
        }
    }

    fun openKeyboard() {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }


    fun hideKeyboard() {
        val immhide = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }


}