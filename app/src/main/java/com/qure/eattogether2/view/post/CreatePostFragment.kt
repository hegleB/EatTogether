package com.qure.eattogether2.view.post

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.qure.eattogether2.R
import com.qure.eattogether2.adapter.CreatePostImageAdapter
import com.qure.eattogether2.data.Post
import com.qure.eattogether2.data.PostImage
import com.qure.eattogether2.data.User
import com.qure.eattogether2.databinding.FragmentCreatePostBinding
import com.qure.eattogether2.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedbottompicker.TedBottomPicker
import gun0912.tedbottompicker.TedBottomSheetDialogFragment
import org.json.JSONArray
import org.json.JSONException
import java.io.File
import java.net.URL
import javax.inject.Inject


@AndroidEntryPoint
class CreatePostFragment : Fragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firebaseStorage: FirebaseStorage

    private lateinit var binding: FragmentCreatePostBinding
    private lateinit var navBar: BottomNavigationView
    private val args by navArgs<CreatePostFragmentArgs>()
    private var imageList: MutableList<Uri> = mutableListOf()
    private val viewModel by viewModels<PostViewModel>()
    private var isPermission: Boolean = false

    private var pref : SharedPreferences? = null
    private var editor : SharedPreferences.Editor? = null




    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        navBar = requireActivity().findViewById(R.id.bottom_navigation)
        navBar.visibility = View.GONE


    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_post, container, false)


        pref = PreferenceManager.getDefaultSharedPreferences(context)
        editor = pref!!.edit()

        viewModel.image.observe(viewLifecycleOwner, {
            writePost(it)

        })
        writePost(arrayListOf())

        requestPermission()
        setPostData()

        moveCreatePostToPostContainer()
        movePCreatePostToCreatePostCategory()

        getAdapter(getStringArrayPref(requireContext(), "images")!!)


        binding.createPostSelectImage.setOnClickListener {

            if (isPermission) {
                openImagesPicker()
            } else {
                requestPermission()
            }
        }

        return binding.root

    }

    fun moveCreatePostToPostContainer() {

        binding.apply {
            createPostToolbar.inflateMenu(R.menu.create_post_menu)
            createPostToolbar.setNavigationIcon(R.drawable.ic_back)
            createPostToolbar.setOnClickListener {
                editor!!.clear()
                editor!!.apply()
                findNavController().popBackStack()
            }
        }


    }

    fun setPostData() {

        binding.apply {
            if (!args.categoryTitle.equals("")) {
                createPostTitle.setText(args.categoryTitle)
            }
            if (!args.categoryName.equals("")) {
                createPostCategory.text = args.categoryName
            }
            if (!args.categoryContent.equals("")) {
                createPostContent.setText(args.categoryContent)
            }
        }

    }

    fun setPostDatabase(
        uid: String,
        writer: String,
        title: String,
        category: String,
        content: String,
        userimage: String,
        timestamp: String,
        key: String,
        likeCount: ArrayList<String>,
        imageList: MutableList<Uri>,
        commentsCount: String,
    ) {


        val post = Post(
            uid,
            writer,
            title,
            category,
            content,
            userimage,
            timestamp,
            key,
            likeCount,
            commentsCount,
            arrayListOf()
        )
        val images = mutableListOf<String>()
        if (post.title.equals("") or post.category.equals("") or post.content.equals("")) {
            Toast.makeText(requireContext(), "빈칸을 채워주세요.", Toast.LENGTH_LONG).show()
        } else {
            binding.createPostProgressbar.visibility = View.VISIBLE

            if (!imageList.isEmpty()) {


                for (i in imageList.indices) {
                    val riverRef: StorageReference =
                        firebaseStorage.getReference()
                            .child("post_image/" + key + "/" + i + ".jpg")


                    val uploadTask: UploadTask = riverRef.putFile(imageList.get(i))


                    uploadTask.addOnSuccessListener {

                        riverRef.downloadUrl.addOnSuccessListener { uri ->
                            firestore.collection("posts").document(key).collection("images")
                                .document().set(PostImage(key, uri.toString()))
                            images.add(uri.toString())
                            firestore.collection("posts").document(key).update("postImages", images)
                            firestore.collection("users").document(uid).get().addOnCompleteListener{ snapshot ->

                                if(snapshot.isSuccessful){
                                    val userData = snapshot.result.toObject(User::class.java)
                                }

                            }
                        }


                    }
                    firestore.collection("posts").document(key).set(post)

                    uploadTask.addOnProgressListener {
                        val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                        if (progress == 100.0) {
                            binding.createPostProgressbar.visibility = View.GONE
                            viewModel.isTrue.postValue(true)

                        }
                    }


                }


                viewModel.isTrue.observe(viewLifecycleOwner, {
                    editor!!.clear()
                    editor!!.apply()
                    findNavController().popBackStack()
                })

            } else {

                firestore.collection("posts").document(key).set(post)
                editor!!.clear()
                editor!!.apply()
                findNavController().popBackStack()
            }

        }
    }

    fun writePost(imageList: MutableList<Uri>) {

        binding.apply {
            createPostToolbar.setOnMenuItemClickListener {

                when (it.itemId) {

                    R.id.menu_create -> {
                        var postTitle = createPostTitle.text.toString()
                        var postCategory = args.categoryName
                        var postContent = createPostContent.text.toString()

                        setPostWriter(postTitle, postCategory, postContent, imageList)

                        true
                    }
                    else -> false

                }
            }

        }

    }

    fun setPostWriter(
        postTitle: String,
        postCategory: String,
        postContent: String,
        imageList: MutableList<Uri>
    ) {

        val currentTime = System.currentTimeMillis().toString()
        val uid = firebaseAuth.currentUser?.uid.toString()
        var user: User?
        val key = firestore.collection("posts").document().id
        val docRef: DocumentReference = firestore.collection("users").document(uid)
        docRef.get().addOnSuccessListener { result ->
            user = result.toObject(User::class.java)


            setPostDatabase(
                uid,
                user!!.usernm,
                postTitle,
                postCategory,
                postContent,
                user!!.userphoto,
                currentTime,
                key,
                arrayListOf(),
                imageList,
                "0"
            )


        }


    }


    fun movePCreatePostToCreatePostCategory() {
        binding.apply {
            binding.createPostCategory.setOnClickListener {


                val image = ArrayList<String>()

                for (i in imageList) {
                    image.add(i.toString())
                }

                val directions =
                    CreatePostFragmentDirections.actionCreatePostFragmentToCreatePostCategoryFragment(
                        createPostTitle.text.toString(),
                        createPostContent.text.toString()
                    )
                findNavController().navigate(directions)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        navBar = requireActivity().findViewById(R.id.bottom_navigation)
        navBar.visibility = View.VISIBLE

    }


    private fun requestPermission() {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                isPermission = true
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

    private fun openImagesPicker() {

        TedBottomPicker.with(requireActivity())
            .setPeekHeight(1600)
            .showGalleryTile(false)
            .setPreviewMaxCount(1000)
            .setSelectMaxCount(3)
            .setSelectMaxCountErrorText("3장 이하로 선택해주세요.")
            .showTitle(false)
            .setTitleBackgroundResId(R.color.white)
            .setGalleryTileBackgroundResId(R.color.white)
            .setSelectedUriList(imageList)
            .setCompleteButtonText("완료")
            .setEmptySelectionText("사진 선택")
            .showMultiImage(object : TedBottomSheetDialogFragment.OnMultiImageSelectedListener {
                override fun onImagesSelected(uriList: MutableList<Uri>?) {

                    if (uriList!!.isEmpty()) {
                        imageList = uriList

                    } else {

                        imageList = uriList!!.reversed() as MutableList<Uri>

                    }
                    getAdapter(imageList)

                    setStringArrayPref("images", imageList)


                }

            })


    }


    fun getAdapter(imageList: MutableList<Uri>) {


        val adapter = CreatePostImageAdapter(
            requireContext(),
            imageList,
            onClickDeleteIcon = {
                imageList.remove(it)
                setStringArrayPref("images", imageList)
                getAdapter(imageList)
                binding.createPostImagerecyclerview.adapter?.notifyDataSetChanged()
            })

        val layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL, false
        )

        binding.createPostImagerecyclerview.layoutManager = layoutManager
        binding.createPostImagerecyclerview.adapter = adapter

        viewModel.image.postValue(imageList)
        binding.createPostImageCount.text = adapter.itemCount.toString()


    }

    private fun setStringArrayPref(key: String, values: MutableList<Uri>) {

        val a = JSONArray()
        for (i in 0 until values.size) {
            a.put(values[i])
        }
        if (!values.isEmpty()) {
            editor!!.putString(key, a.toString())
        } else {
            editor!!.putString(key, null)
        }
        editor!!.apply()


    }

    private fun getStringArrayPref(context: Context, key: String): MutableList<Uri>? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val json = prefs.getString(key, null)

        val urls = mutableListOf<Uri>()
        if (json != null) {
            try {
                val a = JSONArray(json)
                for (i in 0 until a.length()) {
                    var url = a.optString(i)

                    val tmp = Uri.parse(File(url).toString())
                    urls.add(tmp)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return urls
    }

}
