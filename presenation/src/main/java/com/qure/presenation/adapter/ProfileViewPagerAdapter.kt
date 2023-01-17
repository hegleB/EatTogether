package com.qure.presenation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.qure.presenation.view.people.PostCommentsTabFragment
import com.qure.presenation.view.people.PostLikeTabFragment
import com.qure.presenation.view.people.PostWriteTabFragment

class ProfileViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, uid: String) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragmentLaunchers: List<Fragment> = listOf(
        PostWriteTabFragment(uid),
        PostLikeTabFragment(uid),
        PostCommentsTabFragment(uid)
    )

    override fun getItemCount(): Int {
        return fragmentLaunchers.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentLaunchers[position]
    }
}