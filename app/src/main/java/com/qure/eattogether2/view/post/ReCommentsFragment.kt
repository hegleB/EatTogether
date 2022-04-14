package com.qure.eattogether2.view.post

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.qure.eattogether2.R
import com.qure.eattogether2.adapter.CommentsAdapter
import com.qure.eattogether2.adapter.ReCommentsAdapter
import com.qure.eattogether2.data.Comments
import com.qure.eattogether2.data.Post
import com.qure.eattogether2.data.User
import com.qure.eattogether2.databinding.FragmentReCommentsBinding
import com.qure.eattogether2.repository.PostRepository
import com.qure.eattogether2.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReCommentsFragment : Fragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var postRepository: PostRepository

    private val viewModel by viewModels<PostViewModel>()
    private lateinit var binding: FragmentReCommentsBinding
    private val args by navArgs<ReCommentsFragmentArgs>()
    var imm: InputMethodManager? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_re_comments, container, false)


        setRecommentsToolbar()
        changeInputReComments()
        setCommentsData()
        getAllReComments()

        binding.apply {
            seletedRecommentsSendComments.setOnClickListener {
                setRecommentsCommentsDatabase(seletedRecommentsInputComments.text.toString())
                hideKeyboard()
                seletedRecommentsInputComments.setText("")
            }

            val currentUserUid = firebaseAuth.currentUser!!.uid
            if(args.recommentsLikeCount.contains(currentUserUid)){
                selectedRecommentsLike.setTextColor(Color.parseColor("#FFC107"))
            } else {
                selectedRecommentsLike.setTextColor(Color.GRAY)
            }
        }




        return binding.root
    }

    fun setRecommentsToolbar() {
        binding.apply {
            selectedRecommentsToolbar.setNavigationIcon(R.drawable.ic_back)
            selectedRecommentsToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
        }

    }


    fun setCommentsData() {

        val likeCount : ArrayList<String> = arrayListOf()

        for(i in args.recommentsLikeCount){
            likeCount.add(i)
        }

        binding.selectedRecomments =
            Comments(
                args.recommentsUid,
                args.recommentsUsernm,
                args.recommentsUserimage,
                args.recommentsContent,
                args.recommentsTimestamp,
                args.recommentsTimestamp,
                likeCount
            )

    }


    fun getAllReComments() {

        val adapter = ReCommentsAdapter(requireContext(),firestore)
        var liveData = MutableLiveData<List<Comments>>()
        var commentsList : List<Comments> = arrayListOf()

        CoroutineScope(Dispatchers.Main).launch {

            postRepository.getReply(args.recommentsPostkey,args.recommentsCommentskey).collectLatest { comments ->
                for (comment in comments) {

                    commentsList = comments

                }

                commentsList = commentsList
                liveData.postValue(commentsList)
                viewModel.getComments(liveData).collectLatest {
                    adapter.submitData(it)

                }

            }
        }

        binding.selectedRecommentsRecyclerview.adapter = adapter

        val manager = LinearLayoutManager(context)
        manager.reverseLayout = true
        manager.stackFromEnd = true

        binding.selectedRecommentsRecyclerview.layoutManager = manager


    }



    fun changeInputReComments() {
        binding.apply {

            seletedRecommentsInputComments.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    System.out.println("")
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0!!.length > 0) {
                        seletedRecommentsSendComments.visibility = View.VISIBLE
                    } else {
                        seletedRecommentsSendComments.visibility = View.GONE
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                    System.out.println("")
                }

            })

        }
    }

    fun hideKeyboard() {
        imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(binding.seletedRecommentsInputComments.windowToken, 0)
    }


    fun setRecommentsCommentsDatabase(inputText: String) {

        val currentTime = System.currentTimeMillis().toString()
        val uid = firebaseAuth.currentUser?.uid.toString()
        var user: User?

        val docRef: DocumentReference = firestore.collection("users").document(uid)
        docRef.get().addOnSuccessListener { result ->
            user = result.toObject(User::class.java)

            val FirestoreReComments =
                firestore.collection("comments").document(args.recommentsCommentskey)
                    .collection("reply")
                    .document()

            val replyList = Comments(
                user!!.uid,
                user!!.usernm,
                user!!.userphoto,
                inputText,
                args.recommentsTimestamp,
                currentTime,
                arrayListOf(),
                args.recommentsPostkey,
                args.recommentsCommentskey,
                1
            )

            FirestoreReComments.set(replyList)
        }
    }
}
