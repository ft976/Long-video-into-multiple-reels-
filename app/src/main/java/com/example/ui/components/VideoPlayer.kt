package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun VideoPlayer(
    videoUri: String, 
    seekToSeconds: Int? = null, 
    onPositionChanged: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    androidx.compose.runtime.LaunchedEffect(videoUri) {
        val mediaItem = MediaItem.fromUri(videoUri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    androidx.compose.runtime.LaunchedEffect(seekToSeconds) {
        seekToSeconds?.let {
            exoPlayer.seekTo(it * 1000L)
            exoPlayer.playWhenReady = true
        }
    }

    androidx.compose.runtime.LaunchedEffect(exoPlayer, onPositionChanged) {
        if (onPositionChanged != null) {
            while (true) {
                onPositionChanged(exoPlayer.currentPosition)
                kotlinx.coroutines.delay(200)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
            }
        },
        modifier = modifier
    )
}
