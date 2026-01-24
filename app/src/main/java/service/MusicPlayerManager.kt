package service

import android.content.Context
import android.media.MediaPlayer
import com.cleansound.cleansound.R

object MusicPlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    fun init(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(
                context.applicationContext,
                R.raw.diosa
            )
        }
    }

    fun play() {
        mediaPlayer?.let {
            if (!it.isPlaying) it.start()
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) it.pause()
        }
    }

    fun stop() {
        mediaPlayer?.let {
            it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}