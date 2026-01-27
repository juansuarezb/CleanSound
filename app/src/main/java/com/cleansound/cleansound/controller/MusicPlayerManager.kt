package com.cleansound.cleansound.controller

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.cleansound.cleansound.R

object MusicPlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    private var currentUri: Uri? = null

    fun playSong(context: Context, songUri: Uri) {
        try {
            // Si cambia la canción, reiniciar
            if (currentUri != null && currentUri == songUri && mediaPlayer?.isPlaying == true) {
                // ya está reproduciendo la misma canción
                return
            }

            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    setOnPreparedListener { mp -> mp.start() }
                    setOnCompletionListener {
                    }
                }
            } else {
                mediaPlayer?.reset()
            }

            currentUri = songUri
            mediaPlayer?.setDataSource(context, songUri)
            mediaPlayer?.prepareAsync()
        } catch (e: Exception) {
            Log.e("MusicPlayerManager", "Error reproduciendo canción", e)
        }
    }
    fun play() {
        mediaPlayer?.let { if (!it.isPlaying) it.start() }
    }

    fun pause() {
        mediaPlayer?.let { if (it.isPlaying) it.pause() }
    }

    fun stop() {
        mediaPlayer?.let {
            it.stop()
            it.release()
        }
        mediaPlayer = null
        currentUri = null
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true

    fun getDuration(): Int = mediaPlayer?.duration ?: 0

    fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        currentUri = null
    }
}