package com.qure.presenation.view.video

import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.adapter.VideoAdapter
import com.qure.presenation.databinding.FragmentVideoBinding
import com.qure.presenation.viewmodel.VideoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class VideoFragment : BaseFragment<FragmentVideoBinding>(R.layout.fragment_video) {

    private val videoViewModel: VideoViewModel by activityViewModels()

    private lateinit var manager: LinearLayoutManager
    private lateinit var adapter: VideoAdapter

    override fun init() {
        observeViewModel()
        initAdatper()
    }

    private fun observeViewModel() {
        manager = LinearLayoutManager(requireContext())
        adapter = VideoAdapter {
            val direction = VideoFragmentDirections.actionVideoFragmentToVideoDetailFragment(it)
            findNavController().navigate(direction)
        }
        binding.recyclerViewFragmentVideo.layoutManager = manager

        viewLifecycleOwner.lifecycleScope.launch {
            videoViewModel.getYoutubeVideo().collect {
                adapter.submitData(it)
            }
        }

        adapter.addLoadStateListener { combineLoadState ->
            binding.apply {
                spinKitViewFragmentVideoProgressbar.isVisible =
                    combineLoadState.source.refresh is LoadState.Loading
            }
        }
    }

    private fun initAdatper() {
        binding.recyclerViewFragmentVideo.adapter = adapter
    }
}