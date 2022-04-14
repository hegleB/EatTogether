package com.qure.eattogether2.view.home

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.qure.eattogether2.R
import com.qure.eattogether2.common.KeepStateNavigator
import com.qure.eattogether2.data.ChatRoom
import com.qure.eattogether2.databinding.ActivityHomeBinding
import com.qure.eattogether2.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_people.*
import me.leolin.shortcutbadger.ShortcutBadger
import java.lang.NullPointerException
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.home = this


        try {
            setBadge()
        } catch (e: FirebaseFirestoreException) {
            Log.d("TAG", "Firebase Error")
        }


        val navController: NavController = Navigation.findNavController(this, R.id.fragment)


        navController.navigatorProvider.addNavigator(
            KeepStateNavigator(
                this,
                fragment.getChildFragmentManager(),
                R.id.fragment
            )

        )

        navController.setGraph(R.navigation.nav_home)


        NavigationUI.setupWithNavController(bottom_navigation, navController)

        bottom_navigation.setOnNavigationItemSelectedListener(object :
            BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {

                if (item.isChecked) {
                    return false
                }

                when (item.itemId) {
                    R.id.peopleFragment -> {
                        navController.navigate(R.id.peopleFragment, null)

                    }
                    R.id.postFragment -> {
                        navController.navigate(R.id.postFragment, null)

                    }
                    R.id.chatFragment -> {
                        navController.navigate(R.id.chatFragment, null)

                    }
                    R.id.settingFragment -> {
                        navController.navigate(R.id.settingFragment, null)

                    }
                }
                return true
            }

        })


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