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
        ObjectAnimator.ofFloat(binding.textViewSplash, ANIMATOR_PROPERTY_NAME, 0f, 1f).apply {
            duration = DURATION_VALUE
            start()
            Handler(Looper.myLooper()!!).postDelayed({
                fetchWelcome()
            }, FADEIN_DELAY)
        }
    }

    fun fetchWelcome() {
        val uid = firebaseAuth.currentUser?.uid ?: ""
        when (uid.isNullOrEmpty()) {
            true -> findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
            else -> isExistCurrentUser(uid)
        }
    }

    private fun isExistCurrentUser(uid: String) {
        firestore.collection(USERS_COLLECTION_PATH)
            .document(uid)
            .addSnapshotListener { snapshot, e ->
                val isTrue = isExistSnapShoteData(snapshot?.data)
                checkLoginSate(isTrue)
            }
    }

    private fun checkLoginSate(isTrue: Boolean): Any =
        when (isNotExistCurrentUser(isTrue)) {
            true -> findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
            else -> {
                lifecycleScope.launchWhenResumed {
                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToPeopleFragment())
                }
            }
        }

    private fun isNotExistCurrentUser(isTrue: Boolean): Boolean =
        firebaseAuth.currentUser == null || !isTrue

    private fun isExistSnapShoteData(data: Map<String, Any>?): Boolean =
        data != null

    companion object {
        const val DURATION_VALUE = 1700L
        const val FADEIN_DELAY = 2000L
        const val ANIMATOR_PROPERTY_NAME = "alpha"
    }
}
