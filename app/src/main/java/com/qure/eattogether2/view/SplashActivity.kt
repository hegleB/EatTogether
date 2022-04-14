package com.qure.eattogether2.view

import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.qure.eattogether2.R
import com.qure.eattogether2.data.User
import com.qure.eattogether2.prefs.UserPrefrence
import com.qure.eattogether2.view.home.HomeActivity
import com.qure.eattogether2.view.login.MainLoginFragment
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()
        val splashText = findViewById<TextView>(R.id.text_splash)
        val fadeIn = ObjectAnimator.ofFloat(splashText, "alpha", 0f, 1f)
        fadeIn.duration = 1700
        fadeIn.start()

        Handler(Looper.myLooper()!!).postDelayed({
            fetchWelcome()
        }, 2000)

    }


    fun fetchWelcome() {
        val uid = firebaseAuth.currentUser?.uid
        var isTrue = false
        if (uid != null) {
            firestore.collection("users").document(uid).addSnapshotListener { snapshot, e ->

                if (e != null) {

                }
                val data = snapshot?.data


                if (data != null) {
                    isTrue = true
                }

                if (firebaseAuth.currentUser == null || !isTrue) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                    finish()
                }
            }
        } else {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}
