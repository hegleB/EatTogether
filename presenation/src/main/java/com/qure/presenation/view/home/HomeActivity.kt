package com.qure.presenation.view.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.quer.presenation.base.BaseActivity
import com.qure.domain.model.ChatRoom
import com.qure.presenation.R
import com.qure.presenation.databinding.ActivityHomeBinding
import com.qure.presenation.utils.KeepStateNavigator
import dagger.hilt.android.AndroidEntryPoint
import java.lang.NullPointerException
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>(R.layout.activity_home) {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    @Inject
    lateinit var firestore: FirebaseFirestore

    override fun init() {
        try {
            setBadge()
        } catch (e: FirebaseFirestoreException) {
            Log.d("TAG", "Firebase Error")
        }
        val navController: NavController =
            Navigation.findNavController(this, R.id.fragment_activityHome)
        val fagmentMagnger = supportFragmentManager
        val parentFragment = fagmentMagnger.findFragmentById(R.id.fragment_activityHome)

        navController.navigatorProvider.addNavigator(
            KeepStateNavigator(
                this,
                parentFragment!!.getChildFragmentManager(),
                R.id.fragment_activityHome
            )

        )

        navController.setGraph(R.navigation.nav_home)
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        (binding.bottomNavigation.setOnNavigationItemSelectedListener(object :
            BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {

                if (item.isChecked) {
                    return false
                }

                when (item.itemId) {
                    R.id.menuPeopleFragment -> {
                        navController.navigate(R.id.homePeopleFragment, null)

                    }
                    R.id.menuPostFragment -> {
                        navController.navigate(R.id.homePostFragment, null)

                    }
                    R.id.menuChatFragment -> {
                        navController.navigate(R.id.homeChatFragment, null)

                    }
                    R.id.menuSettingFragment -> {
                        navController.navigate(R.id.homeSettingFragment, null)

                    }
                }
                return true
            }

        }))


    }

    fun setBadge() {
        var badge = binding.bottomNavigation.getOrCreateBadge(R.id.chatFragment)
        badge.backgroundColor = ContextCompat.getColor(this@HomeActivity, R.color.light_red)

        val currentId = firebaseAuth.currentUser?.uid
        if (currentId != null) {
            firestore.collection("chatrooms").whereArrayContains("users", currentId!!)
                .addSnapshotListener { snapshot, e ->
                    if (e == null) {

                    }
                    try {
                        var chatCount = snapshot!!.toObjects(ChatRoom::class.java)

                        var count = 0
                        for (i in chatCount) {
                            count += i.unreadCount.get(currentId)!!
                        }
                        if (count == 0) {
                            badge.isVisible = false
                        } else {
                            badge.isVisible = true
                            if (count >= 999) {
                                badge.number = 999
                            } else {
                                badge.number = count
                            }

                        }
                    } catch (e: NullPointerException) {
                        Log.d("TAG", "Null Exception")
                    }

                }


        }

    }

    override fun onBackPressed() {
        super.onBackPressed()

        finish()
    }
}



