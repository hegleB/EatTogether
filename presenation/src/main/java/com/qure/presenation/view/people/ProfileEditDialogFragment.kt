package com.qure.presenation.view.people

import androidx.fragment.app.activityViewModels
import com.qure.presenation.R
import com.qure.presenation.base.BaseDialogFragment
import com.qure.presenation.databinding.FragmentProfileEditDialogBinding
import com.qure.presenation.utils.KeyboardEvent
import com.qure.presenation.utils.ResizeDialog
import com.qure.presenation.viewmodel.PeopleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileEditDialogFragment :
    BaseDialogFragment<FragmentProfileEditDialogBinding>(R.layout.fragment_profile_edit_dialog) {

    private val peopleViewModel: PeopleViewModel by activityViewModels()

    override fun init() {
        initViewModel()
        openKeyboard()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        setUpDialogFragment()
    }

    private fun initViewModel() {
        binding.viewmdoel = peopleViewModel

        peopleViewModel.apply {
            updateEditText()
        }
    }

    private fun observeViewModel() {
        peopleViewModel.profileEditCancle.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            dismiss()
            hideKeyboard()
            it.consume()
        }

        peopleViewModel.profileEditSubmit.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            submitProfile()
            it.consume()
        }
    }

    private fun submitProfile() {
        hideKeyboard()
        peopleViewModel.updateEditText()
    }

    private fun openKeyboard() {
        KeyboardEvent(requireActivity()).openKeyboard()
    }

    private fun hideKeyboard() {
        KeyboardEvent(requireActivity()).hideKeyboard()
    }

    private fun setUpDialogFragment() {
        ResizeDialog(context!!, dialog!!).setUpDialogFragment()
    }
}
