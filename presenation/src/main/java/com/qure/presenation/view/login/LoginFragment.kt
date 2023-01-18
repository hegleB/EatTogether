package com.qure.presenation.view.login

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.databinding.FragmentLoginBinding
import com.qure.presenation.utils.BottomNavigationEvent
import com.qure.presenation.utils.OnBackPressedListener
import com.qure.presenation.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun init() {
        initViewModel()
        observeViewModel()
        OnBackPressedListener().finish(requireActivity(), requireActivity())
        BottomNavigationEvent().hideBottomNavigation(activity!!)
    }

    private fun initViewModel() {
        binding.viewmodel = authViewModel
    }

    private fun observeViewModel() {
        authViewModel.bottomsSheetLogin.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            findNavController().navigate(R.id.action_loginFragment_to_loginBottomSheetDialog)
            it.consume()
        }
    }
}
