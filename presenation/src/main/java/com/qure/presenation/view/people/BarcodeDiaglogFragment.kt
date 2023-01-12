package com.qure.presenation.view.people

import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.viewModels
import com.qure.presenation.R
import com.qure.presenation.base.BaseDialogFragment
import com.qure.presenation.databinding.FragmentBarcodeDiaglogBinding
import com.qure.presenation.utils.ResizeDialog
import com.qure.presenation.viewmodel.PeopleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BarcodeDiaglogFragment :
    BaseDialogFragment<FragmentBarcodeDiaglogBinding>(R.layout.fragment_barcode_diaglog) {

    private val peopleViewModel: PeopleViewModel by viewModels()

    override fun init() {
        initViewModel()
        observeViewModel()
        createBarcode()
        countBarcode()

    }

    override fun onResume() {
        super.onResume()
        setUpDialogFragment()
    }

    private fun initViewModel() {
        binding.viewmodel = peopleViewModel

    }

    private fun observeViewModel() {
        peopleViewModel.cancelBarcode.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            dismiss()
            it.consume()
        }

        peopleViewModel.recreateBarcode.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            createBarcode()
            countBarcode()
            binding.imageButtonFragmentDialogBarcodeRecreateBarcode.visibility = View.INVISIBLE
            it.consume()
        }

    }

    private fun countBarcode() {
        peopleViewModel.countBarcodeTime(15L)
        var mCountDown: CountDownTimer? = null
        mCountDown = object : CountDownTimer(16000, 1000) {
            override fun onTick(l: Long) {
                val num = l / 1000L
                peopleViewModel.countBarcodeTime(num)
            }

            override fun onFinish() {
                peopleViewModel.countBarcodeTime(0L)
                peopleViewModel.deleteBarcode()
                binding.apply {
                    imageButtonFragmentDialogBarcodeRecreateBarcode.visibility = View.VISIBLE
                }
            }
        }.start()
    }

    private fun setUpDialogFragment() {
        ResizeDialog(context!!, dialog!!).setUpDialogFragment()
    }

    private fun createBarcode() {
        var uid = peopleViewModel.getCurrentUser()?.uid
        var random_num = (Math.random() * 100).toString()
        var random_barcode = uid + "_" + random_num;
        peopleViewModel.setBarcode(random_barcode)
        peopleViewModel.createBarcod(random_barcode)
    }

}