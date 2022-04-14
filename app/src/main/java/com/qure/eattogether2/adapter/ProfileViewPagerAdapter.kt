package com.qure.eattogether2.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.qure.eattogether2.data.User
import com.qure.eattogether2.view.people.ProfileCommentPostFragment
import com.qure.eattogether2.view.people.ProfileLikePostFragment
import com.qure.eattogether2.view.people.ProfileWritePostFragment


class ProfileViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, user: User) :
    FragmentStateAdapter(fragmentManager, lifecycle), TabLayoutMediator.TabConfigurationStrategy {

    private val fragmentLaunchers: List<Fragment> = listOf(
        ProfileWritePostFragment(user),
        ProfileLikePostFragment(user),
        ProfileCommentPostFragment(user)
    )

    private val titles = listOf(
        "작성한 글",
        "좋아한 글",
        "댓글단 글"
    )


    override fun getItemCount(): Int {
        return fragmentLaunchers.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentLaunchers[position]
    }

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        tab.text = titles[position]
    }


}
