package com.qure.presenation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth

abstract class BaseDialogFragment<T : ViewDataBinding>(@LayoutRes val layoutResInt: Int) :
    DialogFragment() {

    lateinit var binding: T
    lateinit var currentUid: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutResInt, container, false)
        currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        init()
        binding.lifecycleOwner = this
        return binding.root
    }

    abstract fun init()
}
