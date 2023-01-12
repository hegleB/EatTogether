package com.qure.presenation.view

import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.quer.presenation.base.BaseActivity
import com.qure.domain.model.ChatRoom
import com.qure.presenation.R
import com.qure.presenation.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.NullPointerException
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {


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

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        setupBottomNavMenu(navController)
    }

    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = binding.bottomNavigation
        bottomNav.setupWithNavController(navController)
    }

    fun setBadge() {
        var badge = binding.bottomNavigation.getOrCreateBadge(R.id.chatFragment)
        badge.backgroundColor = ContextCompat.getColor(this@MainActivity, R.color.light_red)

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
}