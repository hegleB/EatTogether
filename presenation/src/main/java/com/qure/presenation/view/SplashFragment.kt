package com.qure.presenation.view

import android.animation.ObjectAnimator
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.quer.presenation.base.BaseFragment
import com.qure.domain.utils.USERS_COLLECTION_PATH
import com.qure.presenation.R
import com.qure.presenation.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    override fun init() {
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
            firestore.collection(USERS_COLLECTION_PATH).document(uid).addSnapshotListener { snapshot, e ->
                if (e != null) {
                }
                val data = snapshot?.data


                if (data != null) {
                    isTrue = true
                }

                if (firebaseAuth.currentUser == null || !isTrue) {
                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
                } else {
                    lifecycleScope.launchWhenResumed {
                        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToPeopleFragment())
                    }
                }
            }
        } else {
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
        }
    }
}