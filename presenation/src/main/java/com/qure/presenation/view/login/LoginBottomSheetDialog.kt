package com.qure.presenation.view.login

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.qure.domain.utils.Resource
import com.qure.presenation.R
import com.qure.presenation.base.BaseBottomSheetFragment
import com.qure.presenation.databinding.DialogLoginBinding
import com.qure.presenation.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginBottomSheetDialog : BaseBottomSheetFragment<DialogLoginBinding>(R.layout.dialog_login) {

    private val authViewModel: AuthViewModel by viewModels()
    private var callbackManager: CallbackManager? = null
    private lateinit var auth: FirebaseAuth
    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        handleSignInResult(result.data)
    }

    @Inject
    lateinit var signInClient: SignInClient
    @Inject
    lateinit var signInRequest : GetSignInIntentRequest

    override fun init() {
        initViewModel()
        observeViewModel()
        auth = Firebase.auth

    }


    private fun initViewModel() {
        binding.viewmodel = authViewModel
    }

    private fun observeViewModel() {
        authViewModel.buttonGoogle.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            loginGoogle()
            it.consume()
        }

        authViewModel.buttonFacebook.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            loginFacebook()
            it.consume()
        }
    }


    private fun loginFacebook() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    authViewModel.accessFacebook(result!!.accessToken)
                    authWithFacebook()
                }

                override fun onCancel() {
                    Snackbar.make(requireView(), "페이스북 로그인 취소", Snackbar.LENGTH_LONG).show()
                }

                override fun onError(error: FacebookException?) {
                    Snackbar.make(requireView(), error?.message.toString(), Snackbar.LENGTH_LONG)
                        .show()
                }

            })
    }


    private fun authWithFacebook() {
        authViewModel.signInFacebookState.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.spinKitViewDialogLoginProgressbar.visibility = View.GONE
                    Log.d("IsJoin", "${it.data?.email}")
                    if (isJoin(it.data!!) == true) {
                        moveHomePage()
                    } else {
                        findNavController().navigate(R.id.action_loginBottomSheetDialog_to_profileSettingFragment)
                    }
                }

                is Resource.Loading -> {
                    binding.spinKitViewDialogLoginProgressbar.visibility = View.VISIBLE
                }

                is Resource.Error -> {
                    binding.spinKitViewDialogLoginProgressbar.visibility = View.GONE
                    Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                }

            }
        }
    }

    private fun loginGoogle() {

        signInClient.getSignInIntent(signInRequest).addOnSuccessListener { pendingIntent ->
                launchSignIn(pendingIntent)
            }
            .addOnFailureListener { e ->
                failGoogleAuthMessage()
            }
    }


    private fun launchSignIn(pendingIntent: PendingIntent) {
        try {
            val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent)
                .build()
            signInLauncher.launch(intentSenderRequest)
        } catch (e: IntentSender.SendIntentException) {
            failGoogleAuthMessage()
        }
    }

    private fun handleSignInResult(data: Intent?) {
        try {
            val credential = signInClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                firebaseAuthWithGoogle(idToken)
            } else {
                failGoogleAuthMessage()
            }
        } catch (e: ApiException) {
            failGoogleAuthMessage()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        authViewModel.accessGoogle(credential)

        authViewModel.signInGoogleState.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.spinKitViewDialogLoginProgressbar.visibility = View.GONE
                    if (isJoin(it.data!!) == true) {
                       moveHomePage()
                    } else {
                        findNavController().navigate(R.id.action_loginBottomSheetDialog_to_profileSettingFragment)
                    }
                }
                is Resource.Loading -> {
                    binding.spinKitViewDialogLoginProgressbar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.spinKitViewDialogLoginProgressbar.visibility = View.GONE
                    Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun isJoin(user: FirebaseUser): Boolean? {
        return authViewModel.isJoin(user)
    }

    private fun moveHomePage() {
        findNavController().navigate(R.id.action_loginBottomSheetDialog_to_peopleContainerFragment)
    }

    private fun failGoogleAuthMessage() {
        Snackbar.make(requireView(), "구글 인증 실패", Snackbar.LENGTH_SHORT).show()
    }
}