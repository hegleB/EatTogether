package com.qure.eattogether2.view.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.qure.eattogether2.R
import com.qure.eattogether2.databinding.FragmentMainLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainLoginFragment : Fragment() {

    private lateinit var binding: FragmentMainLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_login, container, false)


        binding.apply {

            mainLoginLoginButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    findNavController().navigate(R.id.action_MainloginFragment_to_loginFragment)
                    // Create a new user with a first and last name

                }

            })

        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



    }

}