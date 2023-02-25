package com.qure.presenation.view.video


import android.content.pm.ActivityInfo
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.quer.presenation.base.BaseFragment
import com.qure.presenation.R
import com.qure.presenation.databinding.FragmentVideoDetailBinding
import com.qure.presenation.utils.BottomNavigationEvent
import com.qure.presenation.viewmodel.VideoViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class VideoDetailFragment :
    BaseFragment<FragmentVideoDetailBinding>(R.layout.fragment_video_detail) {

    private val args: VideoDetailFragmentArgs by navArgs()
    private val videoViewModel: VideoViewModel by activityViewModels()

    private var _player: SimpleExoPlayer? = null
    private val player: SimpleExoPlayer
        get() = _player!!

    private var fullScreen = false

    private lateinit var fullScreenButton: ImageView

    override fun init() {
        binding.items = args.items
        fullScreenButton =
            binding.playViewFragmentVideoDetailVideo.findViewById(R.id.exo_fullscreen)
        initPlayer()
        observeViewModel()
        initViewModel()
    }

    private fun initViewModel() {
        binding.viewmodel = videoViewModel
        videoViewModel.apply {
            getYoutubeVideoId(requireContext(), args.items.snippet.resourceId.videoId)
        }

        binding.playViewFragmentVideoDetailVideo.findViewById<ImageView>(R.id.exo_fullscreen)
            .setOnClickListener {
                playFullSrcreen()
            }
    }

    private fun initPlayer() {
        _player = SimpleExoPlayer.Builder(requireContext())
            .setUseLazyPreparation(true)
            .build()
        binding.playViewFragmentVideoDetailVideo.player = player
        player.repeatMode = ExoPlayer.REPEAT_MODE_ONE
        player.addListener(playbackStateListener())
    }

    private fun observeViewModel() {
        videoViewModel.toolbarBack.observe(viewLifecycleOwner) {
            if (it.consumed) return@observe
            if (fullScreen) {
                setPortaritScreen()
                fullScreen = !fullScreen
                videoViewModel.isFullscreen(fullScreen)
            } else {
                findNavController().popBackStack()
            }
            it.consume()
        }

        videoViewModel.mediaSource.observe(viewLifecycleOwner) {
            player.run {
                setMediaSource(it)
                prepare()
            }
        }
    }

    private fun playbackStateListener() = object : Player.EventListener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                ExoPlayer.STATE_BUFFERING -> videoViewModel.showProgress()
                ExoPlayer.STATE_READY -> videoViewModel.hideProgress()
            }
        }
    }

    private fun playFullSrcreen() {
        if (fullScreen) {
            setPortaritScreen()
        } else {
            setLandscapeScreen()
        }
        binding.playViewFragmentVideoDetailVideo.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        fullScreen = !fullScreen
        videoViewModel.isFullscreen(fullScreen)

    }

    private fun setLandscapeScreen() {
        fullScreenButton.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_fullscreen_exit_24
            )
        )
        activity!!.setRequestedOrientation(
            if (Build.VERSION.SDK_INT < 9) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
        )
        activity!!.apply {
            window.decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            )
            if (actionBar != null) {
                actionBar!!.hide();
            }
        }
        val params: ConstraintLayout.LayoutParams =
            binding.playViewFragmentVideoDetailVideo.getLayoutParams() as ConstraintLayout.LayoutParams
        params.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        binding.playViewFragmentVideoDetailVideo.setLayoutParams(params)
        BottomNavigationEvent().hideBottomNavigation(activity!!)
    }

    private fun setPortaritScreen() {
        fullScreenButton.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_fullscreen_24
            )
        )
        activity!!.setRequestedOrientation(
            if (Build.VERSION.SDK_INT < 9) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            }
        )
        activity!!.apply {
            window.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE)
            if (actionBar != null) {
                actionBar!!.show();
            }
        }
        val params: ConstraintLayout.LayoutParams =
            binding.playViewFragmentVideoDetailVideo.getLayoutParams() as ConstraintLayout.LayoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = (300 * getApplicationContext().getResources().getDisplayMetrics().density).toInt()
        binding.playViewFragmentVideoDetailVideo.setLayoutParams(params)
        BottomNavigationEvent().showBottomNavigation(activity!!)
    }

    override fun onResume() {
        super.onResume()
        player.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onStop() {
        super.onStop()
        player.run {
            removeListener(playbackStateListener())
            release()
        }
        _player = null
    }
}