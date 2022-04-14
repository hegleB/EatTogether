package com.qure.eattogether2.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.GetTokenResult
import com.qure.eattogether2.R
import com.qure.eattogether2.databinding.FragmentLoginBinding
import com.qure.eattogether2.prefs.UserPrefrence
import com.qure.eattogether2.utils.Resource
import com.qure.eattogether2.view.home.HomeActivity
import com.qure.eattogether2.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : BottomSheetDialogFragment() {

    private val TAG = "LoginFragment"
    private lateinit var binding: FragmentLoginBinding

    private var GOOGLE_LOGIN_CODE = 9001
    private var callbackManager: CallbackManager? = null
    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private var mAuthListener: AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuthListener = AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {



            } else {

            }

        }
    }

    override fun onStart() {
        super.onStart()

//        firebaseAuth.addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
//        firebaseAuth.removeAuthStateListener(mAuthListener!!)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED


        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.apply {
            googleBtn.setOnClickListener { googleLogin() }
            buttonGoogle.setOnClickListener { googleLogin() }
            facebookBtn.setOnClickListener { facebookLogin() }
            buttonFacebook.setOnClickListener { facebookLogin() }

        }

    }

    private fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
        binding.loginProgressbar.visibility = View.VISIBLE
    }


    fun facebookLogin() {
        binding.loginProgressbar.visibility = View.VISIBLE
        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {


                    viewModel.firebaseAuthWithFaceBook(result?.accessToken!!).observe(
                        viewLifecycleOwner,
                        {
                            when (it.status) {
                                Resource.Status.SUCCESS -> {

                                    binding.loginProgressbar.visibility = View.GONE
                                    viewModel.isUser.observe(viewLifecycleOwner, {
                                        if(it){
                                            moveHomePage()
                                        } else {
                                            findNavController().navigate(R.id.action_loginFragment_to_profileSettingFragment)
                                        }
                                    })


                                }
                                Resource.Status.ERROR -> {
                                    binding.loginProgressbar.visibility = View.GONE
                                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG)
                                        .show()
                                }
                                Resource.Status.LOADING -> {

                                    binding.loginProgressbar.visibility = View.VISIBLE

                                }
                            }
                        })

                }

                override fun onCancel() {

                }

                override fun onError(error: FacebookException?) {

                }
            })
    }


    fun moveHomePage() {


        activity?.let {
            val intent = Intent(context, HomeActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }


    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager?.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_LOGIN_CODE) {
            var task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                viewModel.firebaseAuthWithGoogle(account).observe(viewLifecycleOwner, {
                    when (it.status) {
                        Resource.Status.SUCCESS -> {

                            binding.loginProgressbar.visibility = View.GONE
                            viewModel.isUser.observe(viewLifecycleOwner, {
                                System.out.println("유저유무: "+it)
                                if(it){
                                    moveHomePage()
                                } else {
                                    findNavController().navigate(R.id.action_loginFragment_to_profileSettingFragment)
                                }
                            })


                        }
                        Resource.Status.ERROR -> {
                            binding.loginProgressbar.visibility = View.GONE
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }
                        Resource.Status.LOADING -> {

                            binding.loginProgressbar.visibility = View.VISIBLE

                        }
                    }
                })
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }

    }



}