package com.qure.presenation.view.people

import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.databinding.FragmentPeopleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PeopleFragment : BaseFragment<FragmentPeopleBinding>(R.layout.fragment_people) {

    override fun init() {
        println("peopleFragment")
    }

}