package com.qure.presenation.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.qure.domain.utils.Resource
import com.qure.presenation.R
import com.qure.presenation.base.BaseBottomSheetFragment
import com.qure.presenation.databinding.DialogLoginBinding
import com.qure.presenation.view.home.HomeActivity
import com.qure.presenation.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginBottomSheetDialog : BaseBottomSheetFragment<DialogLoginBinding>(R.layout.dialog_login) {

    private val authViewModel: AuthViewModel by viewModels()
    private var GOOGLE_LOGIN_CODE = 9001
    private var callbackManager: CallbackManager? = null

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    override fun init() {
        initViewModel()
        observeViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager?.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_LOGIN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            val acct = task.getResult(ApiException::class.java)
            authViewModel.accessGoogle(acct)
            authWithGoogle()
        }
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

    private fun loginGoogle() {
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
        binding.spinKitViewDialogLoginProgressbar.visibility = View.VISIBLE
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

    private fun authWithGoogle() {

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
        activity?.let {
            val intent = Intent(context, HomeActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
}