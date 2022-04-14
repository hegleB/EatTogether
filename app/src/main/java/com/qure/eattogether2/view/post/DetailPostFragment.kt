package com.qure.eattogether2.view.post

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.qure.eattogether2.R
import com.qure.eattogether2.adapter.CommentsAdapter
import com.qure.eattogether2.adapter.PostsImageAdapter
import com.qure.eattogether2.data.Comments
import com.qure.eattogether2.data.Post
import com.qure.eattogether2.data.PostImage
import com.qure.eattogether2.data.User
import com.qure.eattogether2.databinding.FragmentDetailPostBinding
import com.qure.eattogether2.repository.PostRepository
import com.qure.eattogether2.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_people.*
import kotlinx.android.synthetic.main.fragment_post_container.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class DetailPostFragment : Fragment(){

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var postRepository: PostRepository

    private lateinit var callback: OnBackPressedCallback
    private lateinit var binding: FragmentDetailPostBinding
    private val args by navArgs<DetailPostFragmentArgs>()
    private lateinit var navBar: BottomNavigationView
    private val viewModel by viewModels<PostViewModel>()
    var imm: InputMethodManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)




        navBar = requireActivity().findViewById(R.id.bottom_navigation)
        navBar.visibility = View.GONE


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_post, container, false)



        setPostData()
        setDetailToolbar()
        getAllComments()
        changeInputComments()
        setPostImage()
        setLike()






        viewModel.apply {

            postKey = args.detailPostKey
            likeCount.observe(viewLifecycleOwner, {
                binding.detailPostLikeCount.text = it.toString()
            })

        }


        binding.apply {
            detailPostSendComments.setOnClickListener {
                setCommentsDatabase(detailPostInputComments.text.toString())

                detailPostInputComments.setText("")
                hideKeyboard()


            }

        }



        return binding.root
    }

    override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                if (!args.detailWhere.equals("post")) {
                    val directions =
                        DetailPostFragmentDirections.actionDetailPostFragmentToHiltDetailProfileActivity(
                            "",
                            args.detailUser
                        )

                    findNavController().navigate(directions)




                } else {
                    findNavController().popBackStack()
                }
                return@OnKeyListener true
            }
            false
        })
    }


    fun setDetailToolbar() {
        binding.apply {
            detailPostToolbar.setNavigationIcon(R.drawable.ic_back)
            detailPostToolbar.setOnClickListener {
                if (args.detailWhere.equals("post")) {
                    findNavController().popBackStack()
                } else {
                    val directions =
                        DetailPostFragmentDirections.actionDetailPostFragmentToHiltDetailProfileActivity(
                            "",
                            args.detailUser
                        )

                    findNavController().navigate(directions)

                }
            }
        }

    }



    fun setPostData() {
        val postLikeList: ArrayList<String> = arrayListOf()
        for (i in args.detailPostLikeCount) {
            postLikeList.add(i)
        }

        binding.detailPost =
            Post(
                "",
                args.detailPostUsernm,
                args.detailPostTitle,
                args.detailPostCategory,
                args.detailPostContent,
                args.detailPostProfile,
                args.detailPostTime,
                args.detailPostKey,
                postLikeList,
                ""
            )

    }

    fun changeInputComments() {
        binding.apply {

            detailPostInputComments.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    System.out.println("")
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0!!.length > 0) {
                        detailPostSendComments.visibility = View.VISIBLE
                    } else {
                        detailPostSendComments.visibility = View.GONE
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                    System.out.println("")
                }

            })

        }
    }


    fun setCommentsDatabase(inputText: String) {

        val FirestoreComments =
            firestore.collection("comments").document()

        val commentsId = FirestoreComments.id

        val currentTime = System.currentTimeMillis().toString()
        val uid = firebaseAuth.currentUser?.uid.toString()
        var user: User?
        val docRef: DocumentReference = firestore.collection("users").document(uid)
        docRef.get().addOnSuccessListener { result ->
            user = result.toObject(User::class.java)


            val comments = Comments(
                user!!.uid,
                user!!.usernm,
                user!!.userphoto,
                inputText,
                currentTime,
                currentTime,
                arrayListOf(),
                args.detailPostKey,
                commentsId,
                0
            )
            FirestoreComments.set(comments)

        }
    }


    fun getAllComments() {


        val adapter = CommentsAdapter(requireContext(), firestore, viewModel, postRepository)
        var commentsList: List<Comments> = listOf()
        var liveData = MutableLiveData<List<Comments>>()
        CoroutineScope(Dispatchers.Main).launch {

            postRepository.getAllComments(args.detailPostKey).collectLatest { comments ->
                for (comment in comments) {

                    commentsList = comments

                }
                binding.detailPostCommentsCount.text = commentsList.size.toString()

                firestore.collection("posts")
                    .document(args.detailPostKey)
                    .update("commentsCount", commentsList.size.toString())


                if (!commentsList.isEmpty()) {
                    binding.detailPostCommentRecyclerview.visibility = View.VISIBLE
                    binding.detailPostCommentNoRecyclerview.visibility = View.GONE
                    commentsList = commentsList.reversed()
                    liveData.postValue(commentsList)
                    viewModel.getComments(liveData).collectLatest {
                        adapter.submitData(it)

                    }

                } else {
                    binding.detailPostCommentRecyclerview.visibility = View.GONE
                    binding.detailPostCommentNoRecyclerview.visibility = View.VISIBLE
                }

            }

        }
        binding.detailPostCommentRecyclerview.adapter = adapter

    }

    fun setPostImage() {
        firestore.collectionGroup("images")
            .whereEqualTo("postkey", args.detailPostKey)
            .orderBy("imagePath", Query.Direction.ASCENDING).addSnapshotListener { task, e ->

                if (e != null) {
                    return@addSnapshotListener
                }

                val imageList: MutableList<String> = mutableListOf()

                val s = task!!.toObjects(PostImage::class.java)
                if (!s.isEmpty()) {

                    binding.detailPostProgressbar.visibility = View.VISIBLE

                    for (i in s) {

                        imageList.add(i.imagePath)


                    }

                    val imageAdapter = PostsImageAdapter(
                        requireContext(),
                        imageList,
                        binding.detailPostProgressbar
                    )
                    val layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL, false
                    )
                    binding.detailPostImageRecyclerview.adapter = imageAdapter
                    binding.detailPostImageRecyclerview.layoutManager = layoutManager

                }
            }
    }


    fun hideKeyboard() {
        imm = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(binding.detailPostInputComments.windowToken, 0)
    }

    fun setLike() {
        val likeList = args.detailPostLikeCount.toMutableList()
        val currentUser = firebaseAuth.currentUser?.uid


        if (args.detailPostLikeCount.contains(currentUser)) {
            binding.detailPostLikeFill.visibility = View.VISIBLE
        } else {
            binding.detailPostLikeFill.visibility = View.GONE
        }

        binding.detailPostLike.setOnClickListener {
            binding.detailPostLikeFill.visibility = View.VISIBLE

            likeList.add(currentUser!!)
            firestore.collection("posts").document(args.detailPostKey).update("likecount", likeList)
            viewModel.likeCount.value = likeList.size
        }



        binding.detailPostLikeFill.setOnClickListener {
            binding.detailPostLikeFill.visibility = View.GONE
            likeList.remove(currentUser)
            firestore.collection("posts").document(args.detailPostKey).update("likecount", likeList)
            viewModel.likeCount.value = likeList.size
        }

    }


    override fun onDestroy() {
        super.onDestroy()

        navBar = requireActivity().findViewById(R.id.bottom_navigation)
        navBar.visibility = View.VISIBLE

    }


}