package com.cleansound.cleansound.controller

import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cleansound.cleansound.R

class MiniPlayerController(private val activity: AppCompatActivity) : PlaybackManager.Listener {

    // ✅ Buscar el root de 2 formas (según cómo esté inflado en cada XML)
    private val miniRoot: View? by lazy {
        activity.findViewById<View?>(R.id.miniPlayer)
            ?: activity.findViewById<View?>(R.id.bottomPlayer)
    }

    // ✅ Buscar todo SIEMPRE dentro del root (no directo en activity)
    private val tvTitle: TextView? by lazy { miniRoot?.findViewById(R.id.tvMiniSongTitle) }
    private val tvArtist: TextView? by lazy { miniRoot?.findViewById(R.id.tvMiniArtist) }
    private val btnPrev: ImageButton? by lazy { miniRoot?.findViewById(R.id.btnMiniPrevious) }
    private val btnPlayPause: ImageButton? by lazy { miniRoot?.findViewById(R.id.btnMiniPlayPause) }
    private val btnNext: ImageButton? by lazy { miniRoot?.findViewById(R.id.btnMiniNext) }
    private val seekBar: SeekBar? by lazy { miniRoot?.findViewById(R.id.miniSeekBar) }
    private val imgArt: ImageView? by lazy { miniRoot?.findViewById(R.id.ivMiniAlbumArt) }

    private var userSeeking = false

    fun bind() {
        // Si esta activity no tiene mini player, no hacemos nada (NO crashea)
        val root = miniRoot ?: return

        btnPrev?.setOnClickListener { PlaybackManager.previous(activity) }
        btnNext?.setOnClickListener { PlaybackManager.next(activity) }
        btnPlayPause?.setOnClickListener { PlaybackManager.togglePlayPause() }

        // Abrir NowPlaying como Spotify
        root.setOnClickListener {
            activity.startActivity(Intent(activity, NowPlayingActivity::class.java))
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(sb: SeekBar?) { userSeeking = true }

            override fun onStopTrackingTouch(sb: SeekBar?) {
                userSeeking = false
                val target = sb?.progress ?: 0
                PlaybackManager.seekTo(target)
            }
        })
    }

    fun start() {
        if (miniRoot == null) return
        PlaybackManager.addListener(this)
    }

    fun stop() {
        if (miniRoot == null) return
        PlaybackManager.removeListener(this)
    }

    override fun onStateChanged(state: PlaybackManager.State) {
        val root = miniRoot ?: return

        root.visibility = if (state.hasSong) View.VISIBLE else View.GONE
        if (!state.hasSong) return

        tvTitle?.text = state.title
        tvArtist?.text = state.artist

        btnPlayPause?.setImageResource(
            if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
        )

        if (!userSeeking) {
            val max = state.durationMs.coerceAtLeast(1)
            seekBar?.max = max
            seekBar?.progress = state.positionMs.coerceIn(0, max)
        }

    }
}
