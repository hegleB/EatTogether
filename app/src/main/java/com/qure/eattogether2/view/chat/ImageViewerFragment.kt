package com.qure.eattogether2.view.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.qure.eattogether2.R
import com.qure.eattogether2.databinding.FragmentImageViewerBinding

class ImageViewerFragment : Fragment() {

    private lateinit var binding : FragmentImageViewerBinding
    private val args by navArgs<ImageViewerFragmentArgs>()
    private lateinit var navBar: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_image_viewer, container, false)

        binding.apply {

            Glide.with(imageViewer.context).load(args.imageUri).placeholder(R.drawable.anim_loading).into(imageViewer)

            imageViewerClose.setOnClickListener{

                findNavController().popBackStack()
            }
        }

        return binding.root
    }


}