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
    private var mCountDown: CountDownTimer? = null

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
            peopleViewModel.deleteBarcode()
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
        peopleViewModel.countBarcodeTime(COUNT_BARCODE_TIME)
        mCountDown = object : CountDownTimer(BARCODE_COUNTDOWN_MILLS, BARCOD_COUNTDOWN_INTERVAL) {
            override fun onTick(l: Long) {
                val num = l / BARCOD_COUNTDOWN_INTERVAL
                peopleViewModel.countBarcodeTime(num)
            }

            override fun onFinish() {
                peopleViewModel.countBarcodeTime(INIT_BARCODE_TIME)
                peopleViewModel.deleteBarcode()
                binding.imageButtonFragmentDialogBarcodeRecreateBarcode.visibility = View.VISIBLE
            }
        }.start()
    }

    private fun setUpDialogFragment() {
        ResizeDialog(context!!, dialog!!).setUpDialogFragment()
    }

    private fun createBarcode() {
        peopleViewModel.getCurrentUser(currentUid)
        val random_num = (Math.random() * RANDOM_VALUE).toString()
        val random_barcode = currentUid + "_" + random_num
        peopleViewModel.setBarcode(random_barcode)
        peopleViewModel.createBarcod(random_barcode)
    }

    companion object {
        const val COUNT_BARCODE_TIME = 15000L
        const val BARCODE_COUNTDOWN_MILLS = 16000L
        const val BARCOD_COUNTDOWN_INTERVAL = 1000L
        const val INIT_BARCODE_TIME = 0L
        const val RANDOM_VALUE = 100
    }
}
