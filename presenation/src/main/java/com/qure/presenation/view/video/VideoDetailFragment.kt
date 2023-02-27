package com.qure.presenation.view.video


import android.content.pm.ActivityInfo
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
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
import com.qure.presenation.utils.KeyboardEvent
import com.qure.presenation.utils.OnBackPressedListener
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
        OnBackPressedListener().back(activity!!, findNavController())
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
            if (!fullScreen) {
                setVideoScreen()
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
        setVideoScreen()
        binding.playViewFragmentVideoDetailVideo.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
    }

    private fun setVideoScreen() {
        changeFullscreenIcon()
        rotateScreen()
        setActionBar()
        val params: ConstraintLayout.LayoutParams = setLayoutParams()
        binding.playViewFragmentVideoDetailVideo.setLayoutParams(params)
        setBottomNavigation()
        videoViewModel.setFullscreen(fullScreen)
        fullScreen = !fullScreen
    }

    private fun setBottomNavigation() {
        if (fullScreen) {
            return BottomNavigationEvent().hideBottomNavigation(activity!!)
        }
        return BottomNavigationEvent().showBottomNavigation(activity!!)
    }

    private fun setLayoutParams(): ConstraintLayout.LayoutParams {
        val params: ConstraintLayout.LayoutParams =
            binding.playViewFragmentVideoDetailVideo.getLayoutParams() as ConstraintLayout.LayoutParams
        params.apply {
            if (fullScreen) {
                width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                return params
            }
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height =
                (300 * getApplicationContext().getResources().getDisplayMetrics().density).toInt()
        }
        return params
    }

    private fun setActionBar() {
        activity!!.apply {
            setWindowDecorView()
            if (actionBar != null) {
                setActionbar()
            }
        }
    }

    private fun FragmentActivity.setActionbar() {
        if (fullScreen) {
            return actionBar!!.hide()
        }
        return actionBar!!.show()
    }

    private fun FragmentActivity.setWindowDecorView() {
        if (fullScreen) {
            return window.decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            )
        }
        return window.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE)
    }

    private fun rotateScreen() {
        activity!!.setRequestedOrientation(
            if (Build.VERSION.SDK_INT < 9) {
                if (fullScreen) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                if (fullScreen) ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            }
        )
    }

    private fun changeFullscreenIcon() {
        fullScreenButton.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                if (fullScreen) R.drawable.ic_baseline_fullscreen_exit_24 else R.drawable.ic_baseline_fullscreen_24
            )
        )
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