package com.qure.eattogether2.view.people

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.qure.eattogether2.R
import com.qure.eattogether2.adapter.PeopleAdapter
import androidx.paging.LoadState.Loading
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.qure.eattogether2.bindingadapter.ImageBindingAdapter
import com.qure.eattogether2.data.*
import com.qure.eattogether2.databinding.FragmentPeopleBinding
import com.qure.eattogether2.paging.PeoplePagingSource
import com.qure.eattogether2.paging.PostPagingSource
import com.qure.eattogether2.repository.PeopleRepository

import com.qure.eattogether2.viewmodel.PeopleViewModel
import com.yy.mobile.rollingtextview.CharOrder
import com.yy.mobile.rollingtextview.RollingTextView
import com.yy.mobile.rollingtextview.strategy.Direction
import com.yy.mobile.rollingtextview.strategy.Strategy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class PeopleFragment : Fragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var peopleRepository: PeopleRepository

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firebaseStorage: FirebaseStorage

    private val adapter = PeopleAdapter({ User -> moveToProfileFragment(User) })
    private val viewModel by viewModels<PeopleViewModel>()
    private lateinit var binding: FragmentPeopleBinding


    private lateinit var navBar: BottomNavigationView
    private var allUsers = MutableLiveData<MutableList<User>>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_people, container, false)
        var uid = firebaseAuth.currentUser!!.uid


        getMyProfile()


        binding.peopleSwipe.setOnRefreshListener {




            binding.peopleSwipe.isRefreshing = false

        }

        getAllUser()
        setUserAdapter()
//        setProgressBarAccordingToLoadState()

        firestore.collection("users").document(uid).get()
            .addOnCompleteListener { snapshot ->
                val data = snapshot.result.toObject(User::class.java)


                binding.peopleMyImage.setOnClickListener {
                    moveToMyProfile(data!!)
                }

                getProfileCountInfo(data!!)
            }


        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar = requireActivity().findViewById(R.id.bottom_navigation)
        navBar.menu.findItem(R.id.peopleFragment)
            .setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener {
                override fun onMenuItemClick(p0: MenuItem?): Boolean {
                    binding.userRecyclerview.smoothScrollToPosition(0)

                    return false
                }

            })
    }

    fun getMyProfile() {


        firestore.collection("users").document(firebaseAuth.currentUser!!.uid)
            .get().addOnCompleteListener { result ->

                val profile = result!!.result.toObject(User::class.java)

                ImageBindingAdapter.userImage(binding.peopleMyImage, profile!!.userphoto)
                binding.peopleMyName.setText(profile!!.usernm)
                binding.peopleMyMsg.setText(profile!!.usermsg)
            }

    }

    fun setUserAdapter() {

        binding.userRecyclerview.adapter = adapter

    }


    fun getAllUser() {

        CoroutineScope(Dispatchers.Main).launch {
            peopleRepository.getAllUser().collectLatest {
                val userList = mutableListOf<User>()
                for (i in it) {
                    if (!FirebaseAuth.getInstance().currentUser!!.uid.equals(i.uid)) {
                        userList.add(i)
                    }
                }

                adapter.submitList(userList)

            }
        }
    }

    fun getProfileCountInfo(user: User){
        binding.apply {
            firestore.collection("posts").whereEqualTo("uid", user.uid).get()
                .addOnCompleteListener { snapshot ->
                    if (snapshot.isSuccessful) {
                        val postCount = snapshot.result.toObjects(Post::class.java)
                        startCountAnimation(peoplePostCount, postCount.size.toString())
                    }
                }

            firestore.collection("posts").whereArrayContains("likecount", user!!.uid).get()
                .addOnCompleteListener { snapshot ->
                    if (snapshot.isSuccessful) {
                        val likeCount = snapshot.result.toObjects(Post::class.java)
                        System.out.println("likeCount: " + likeCount)
                        startCountAnimation(peopleLikeCount, likeCount.size.toString())
                    }
                }

            firestore.collection("meeting").document(user!!.uid).get().addOnCompleteListener{
                snaphot ->
                if (snaphot.isSuccessful){
                    val meetingCount = snaphot.result.toObject(BarcodeScan::class.java)
                    startCountAnimation(peopleMeetingCount, meetingCount!!.meeting.toString())

                }
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


    fun moveToMyProfile(

        user: User
    ) {


//        val direction = PeopleFragmentDirections.actionPeopleContainerFragmentToDetailProfileActivity("",user)
//
//        findNavController().navigate(direction)

        val intent = Intent(requireActivity(),DetailProfileActivity::class.java)
        intent.putExtra("editData", "")
        intent.putExtra("user",user) //데이터 넣기
        startActivity(intent)
        requireActivity().finish()


    }

    fun moveToProfileFragment(user: User) {

        val direction = PeopleFragmentDirections.actionPeopleFragment2ToProfileFragment(
            user.uid
        )

        findNavController().navigate(direction)
    }


}