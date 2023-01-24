package com.qure.presenation.view.people

import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.quer.presenation.base.BaseFragment
import com.qure.domain.model.ChatRoom
import com.qure.domain.utils.CHATROOMS_COLLECTION_PATH
import com.qure.domain.utils.USERS_FIELD
import com.qure.presenation.R
import com.qure.presenation.adapter.PeopleAdapter
import com.qure.presenation.databinding.FragmentPeopleBinding
import com.qure.presenation.utils.BottomNavigationEvent
import com.qure.presenation.utils.OnBackPressedListener
import com.qure.presenation.viewmodel.PeopleViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PeopleFragment : BaseFragment<FragmentPeopleBinding>(R.layout.fragment_people) {

    private val peopleViewModel: PeopleViewModel by activityViewModels()

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val adapter: PeopleAdapter by lazy {
        PeopleAdapter({ moveToProfileFragment(it.uid) })
    }

    override fun init() {
        BottomNavigationEvent().showBottomNavigation(activity!!)
        observeViewModel()
        initAdapter()
        initViewModel()
        OnBackPressedListener().finish(requireActivity(), requireActivity())
        setBadge()
    }

    private fun initViewModel() {
        binding.viewmodel = peopleViewModel

        peopleViewModel.apply {

            getUserInfo(this@PeopleFragment.currentUid)
            getPostCount(this@PeopleFragment.currentUid)
            getLikeCount(this@PeopleFragment.currentUid)
            getMeetingCount(this@PeopleFragment.currentUid)
            refreshAdapter()
        }
    }

    private fun refreshAdapter() {
        binding.swipeRefreshLayoutFragmentPeople.setOnRefreshListener {
            adapter.notifyDataSetChanged()

            binding.swipeRefreshLayoutFragmentPeople.isRefreshing = false
        }
    }

    private fun observeViewModel() {
        val uid = currentUid
        peopleViewModel.myProfileImage.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            peopleViewModel.setClosedProfileState()
            peopleViewModel.setQRAbledState()
            moveMyProfile(uid)
            it.consume()
        }

        peopleViewModel.userList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        peopleViewModel.user.observe(viewLifecycleOwner) {
            peopleViewModel.getAllUser(it)
        }
    }

    private fun initAdapter() {
        binding.recyclerViewFragmentPeople.adapter = adapter
    }

    private fun moveMyProfile(uid: String) {
        val direction = PeopleFragmentDirections.actionPeopleFragmentToProfileDetailFragment(
            uid,
        )
        findNavController().navigate(direction)
    }

    fun moveToProfileFragment(uid: String) {
        val direction = PeopleFragmentDirections.actionPeopleFragmentToProfileDialogFragment(
            uid,
        )
        findNavController().navigate(direction)
    }

    private fun setBadge() {
        val badge = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!
            .getOrCreateBadge(R.id.chatFragment)
        badge.backgroundColor = ContextCompat.getColor(requireContext(), R.color.light_red)
        val currentId = firebaseAuth.currentUser?.uid
        if (!currentId.isNullOrEmpty()) {
            findMyChatroomMessage(currentId, badge)
        }
    }

    private fun findMyChatroomMessage(currentId: String, badge: BadgeDrawable) {
        firestore.collection(CHATROOMS_COLLECTION_PATH).whereArrayContains(USERS_FIELD, currentId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val count =
                        getMessageCount(snapshot.toObjects(ChatRoom::class.java), currentId)
                    setBadgeState(count, badge)
                }
            }
    }

    private fun setBadgeState(count: Int, badge: BadgeDrawable) {
        badge.isVisible = if (count == 0) false else true
        badge.number = if (count > MAX_MESSAGE_COUNT) MAX_MESSAGE_COUNT else count
    }

    private fun getMessageCount(chaRooms: List<ChatRoom>, currentId: String): Int {
        var messageCount = 0
        for (chatroom in chaRooms) {
            messageCount += chatroom.unreadCount.get(currentId) ?: 0
        }
        return messageCount
    }

    companion object {
        const val MAX_MESSAGE_COUNT = 999
    }
}
