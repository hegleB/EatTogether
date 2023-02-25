package com.qure.presenation.utils

import android.content.Context
import android.util.SparseArray
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.google.android.exoplayer2.util.Util

interface YoutubeExtractorListener {
    fun onExtractionComplete(url: String)
}

class YoutubeExtractor(context: Context, private val listener: YoutubeExtractorListener) :
    YouTubeExtractor(context) {
    override fun onExtractionComplete(
        ytFiles: SparseArray<YtFile>?,
        videoMeta: VideoMeta?
    ) {
        val iTags = 18
        val videoPath = ytFiles?.get(iTags)?.url ?: ""

        if (Util.SDK_INT >= 24) {
            listener.onExtractionComplete(videoPath)
        }
    }
}