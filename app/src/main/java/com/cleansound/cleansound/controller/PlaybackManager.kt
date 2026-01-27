package com.cleansound.cleansound.controller

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import model.Song

object PlaybackManager {

    interface Listener {
        fun onStateChanged(state: State)
    }

    data class State(
        val hasSong: Boolean = false,
        val title: String = "",
        val artist: String = "",
        val isPlaying: Boolean = false,
        val positionMs: Int = 0,
        val durationMs: Int = 0,
        val index: Int = 0,
        val queueSize: Int = 0
    )

    private var mediaPlayer: MediaPlayer? = null
    private var queue: List<Song> = emptyList()
    private var currentIndex: Int = -1

    private var lastContext: Context? = null

    private val listeners = mutableSetOf<Listener>()

    private val handler = Handler(Looper.getMainLooper())
    private val progressTick = object : Runnable {
        override fun run() {
            notifyListeners()
            handler.postDelayed(this, 500)
        }
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
        notifyListeners()
        startTickingIfNeeded()
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
        stopTickingIfNeeded()
    }

    fun setQueue(songs: List<Song>, startIndex: Int, context: Context) {
        if (songs.isEmpty()) return
        lastContext = context.applicationContext
        queue = songs
        currentIndex = startIndex.coerceIn(0, songs.lastIndex)
        playCurrent(context)
    }

    fun togglePlayPause() {
        val mp = mediaPlayer ?: return
        if (mp.isPlaying) mp.pause() else mp.start()
        notifyListeners()
        startTickingIfNeeded()
    }

    fun next(context: Context) {
        lastContext = context.applicationContext
        if (queue.isEmpty()) return
        if (currentIndex < queue.lastIndex) {
            currentIndex++
            playCurrent(context)
        } else {
            notifyListeners()
        }
    }

    fun previous(context: Context) {
        lastContext = context.applicationContext
        if (queue.isEmpty()) return
        val mp = mediaPlayer
        if (mp != null && mp.currentPosition > 3000) {
            mp.seekTo(0)
            notifyListeners()
            return
        }
        if (currentIndex > 0) {
            currentIndex--
            playCurrent(context)
        } else {
            notifyListeners()
        }
    }

    // ✅ Wrappers por si alguien llama sin context
    fun next() {
        val ctx = lastContext ?: return
        next(ctx)
    }

    fun previous() {
        val ctx = lastContext ?: return
        previous(ctx)
    }

    fun seekTo(ms: Int) {
        val mp = mediaPlayer ?: return
        val safe = ms.coerceIn(0, mp.duration)
        mp.seekTo(safe)
        notifyListeners()
    }

    fun getState(): State {
        val mp = mediaPlayer
        val has = currentIndex in queue.indices
        val title = if (has) queue[currentIndex].title else ""
        val artist = if (has) queue[currentIndex].artist else ""
        return State(
            hasSong = has,
            title = title,
            artist = artist,
            isPlaying = mp?.isPlaying == true,
            positionMs = mp?.currentPosition ?: 0,
            durationMs = mp?.duration ?: 0,
            index = currentIndex.coerceAtLeast(0),
            queueSize = queue.size
        )
    }

    fun getCurrentSong(): Song? {
        return if (currentIndex in queue.indices) queue[currentIndex] else null
    }

    private fun playCurrent(context: Context) {
        if (currentIndex !in queue.indices) return

        val song = queue[currentIndex]
        val songUri = Uri.parse(song.uri)

        releasePlayer()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, songUri)
            setOnPreparedListener {
                start()
                notifyListeners()
                startTickingIfNeeded()
            }
            setOnCompletionListener {
                if (currentIndex < queue.lastIndex) {
                    currentIndex++
                    playCurrent(context)
                } else {
                    notifyListeners()
                }
            }
            prepareAsync()
        }
    }

    private fun notifyListeners() {
        val state = getState()
        listeners.forEach { it.onStateChanged(state) }
    }

    private fun startTickingIfNeeded() {
        if (mediaPlayer != null) {
            handler.removeCallbacks(progressTick)
            handler.post(progressTick)
        }
    }

    private fun stopTickingIfNeeded() {
        if (listeners.isEmpty()) {
            handler.removeCallbacks(progressTick)
        }
    }

    private fun releasePlayer() {
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
