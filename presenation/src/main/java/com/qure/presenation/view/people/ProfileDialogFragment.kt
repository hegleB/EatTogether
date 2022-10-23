package com.qure.presenation.view.people

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.qure.presenation.R
import com.qure.presenation.base.BaseBottomSheetFragment
import com.qure.presenation.databinding.DialogProfileBinding
import com.qure.presenation.viewmodel.PeopleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDialogFragment : BaseBottomSheetFragment<DialogProfileBinding>(R.layout.dialog_profile) {

    private val peopleViewModel : PeopleViewModel by activityViewModels()
    private val args : ProfileDialogFragmentArgs by navArgs()

    override fun init() {
        initViewModel()
        observeViewModel()
    }

    private fun initViewModel() {
        binding.viewmodel = peopleViewModel
    }

    private fun observeViewModel() {

        peopleViewModel.otherProfileInfo.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            moveToProfile(args.peopleOtherPersonUid)
            it.consume()
        }
    }

    private fun moveToProfile(uid: String) {

        val direction = ProfileDialogFragmentDirections.actionProfileDialogFragmentToProfileDetailFragment(
            uid
        )
        findNavController().navigate(direction)
    }

}