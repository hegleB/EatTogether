package com.qure.presenation.view.people

import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.adapter.PeopleAdapter
import com.qure.presenation.databinding.FragmentPeopleBinding
import com.qure.presenation.utils.BottomNavigationEvent
import com.qure.presenation.utils.OnBackPressedListener
import com.qure.presenation.viewmodel.PeopleViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PeopleFragment : BaseFragment<FragmentPeopleBinding>(R.layout.fragment_people) {

    private val peopleViewModel: PeopleViewModel by activityViewModels()

    private val adapter: PeopleAdapter by lazy {
        PeopleAdapter({ moveToProfileFragment(it.uid) })
    }


    override fun init() {
        BottomNavigationEvent().showBottomNavigation(activity!!)
        observeViewModel()
        initAdapter()
        initViewModel()
        OnBackPressedListener().finish(requireActivity(), requireActivity())
    }


    private fun initViewModel() {
        binding.viewmodel = peopleViewModel

        peopleViewModel.apply {
            val uid = getCurrentUser()?.uid ?: ""
            getUserInfo(uid)
            getPostCount(uid)
            getLikeCount(uid)
            getMeetingCount(uid)
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
        val uid = peopleViewModel.getCurrentUser()?.uid ?: ""
        peopleViewModel.myProfileImage.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            moveMyProfile(uid)
            it.consume()
        }

        peopleViewModel.userList.observe(viewLifecycleOwner) {
            adapter.submitList(it)

        }

        peopleViewModel.user.observe(viewLifecycleOwner) {
            peopleViewModel.getAllUser(it)
        }

        peopleViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.spinKitViewFragmentPeopleProgressbar.visibility = View.VISIBLE
            } else {
                binding.spinKitViewFragmentPeopleProgressbar.visibility = View.GONE
            }
        }

    }

    private fun initAdapter() {
        binding.recyclerViewFragmentPeople.adapter = adapter
    }

    private fun moveMyProfile(uid: String) {

        val direction = PeopleFragmentDirections.actionPeopleFragmentToProfileDetailFragment(
            uid
        )
        findNavController().navigate(direction)
    }

    fun moveToProfileFragment(uid: String) {
        val direction = PeopleFragmentDirections.actionPeopleFragmentToProfileDialogFragment(
            uid
        )
        findNavController().navigate(direction)
    }
}