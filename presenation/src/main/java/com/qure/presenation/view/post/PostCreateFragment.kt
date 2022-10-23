package com.qure.presenation.view.post

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.databinding.FragmentPostCreateBinding
import com.qure.presenation.utils.BottomNavigationEvent
import com.qure.presenation.utils.OnBackPressedListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostCreateFragment : BaseFragment<FragmentPostCreateBinding>(R.layout.fragment_post_create) {


    override fun init() {
        BottomNavigationEvent().hideBottomNavigation(activity!!)
        initViewModel()
        OnBackPressedListener().back(requireActivity(), findNavController())
    }

    private fun initViewModel() {

    }

}