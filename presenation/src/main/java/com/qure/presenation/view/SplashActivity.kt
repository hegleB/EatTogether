package com.qure.presenation.view

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.quer.presenation.base.BaseActivity
import com.qure.presenation.R
import com.qure.presenation.databinding.ActivitySplashBinding
import com.qure.presenation.view.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    @Inject
    lateinit var firestore: FirebaseFirestore

    override fun init() {
        supportActionBar?.hide()
        val fadeIn = ObjectAnimator.ofFloat(binding.textViewSplash, "alpha", 0f, 1f)
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