package com.cleansound.cleansound.controller

import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cleansound.cleansound.R

class NowPlayingActivity : AppCompatActivity(), PlaybackManager.Listener {

    private lateinit var ivAlbumArt: ImageView
    private lateinit var tvSongTitle: TextView
    private lateinit var tvArtist: TextView
    private lateinit var tvToolbarTitle: TextView

    private lateinit var btnFavorite: ImageButton
    private lateinit var btnPlayPause: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var btnPrevious: ImageButton
    private lateinit var btnBack: ImageButton

    private lateinit var seekBar: SeekBar
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvTotalTime: TextView

    private var userSeeking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_now_playing)

        initViews()
        setupListeners()

        // Render inicial (por si ya hay canción reproduciéndose)
        render(PlaybackManager.getState())
    }

    private fun initViews() {
        ivAlbumArt = findViewById(R.id.ivAlbumArt)
        tvSongTitle = findViewById(R.id.tvSongTitle)
        tvArtist = findViewById(R.id.tvArtist)
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle)

        btnFavorite = findViewById(R.id.btnFavorite)
        btnPlayPause = findViewById(R.id.btnPlayPause)
        btnNext = findViewById(R.id.btnNext)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnBack = findViewById(R.id.btnBack)

        seekBar = findViewById(R.id.seekBar)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)
        tvTotalTime = findViewById(R.id.tvTotalTime)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }

        btnPlayPause.setOnClickListener {
            PlaybackManager.togglePlayPause()
        }

        btnNext.setOnClickListener {
            PlaybackManager.next(this)
        }

        btnPrevious.setOnClickListener {
            PlaybackManager.previous(this)
        }

        // Favorito: toggle visual simple
        btnFavorite.setOnClickListener {
            val selected = btnFavorite.tag as? Boolean ?: false
            val newState = !selected
            btnFavorite.tag = newState
            btnFavorite.setImageResource(
                if (newState) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )
        }

        // SeekBar en MILISEGUNDOS (no porcentaje)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(sb: SeekBar?) {
                userSeeking = true
            }

            override fun onStopTrackingTouch(sb: SeekBar?) {
                userSeeking = false
                val targetMs = sb?.progress ?: 0
                PlaybackManager.seekTo(targetMs)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        PlaybackManager.addListener(this)
    }

    override fun onStop() {
        super.onStop()
        PlaybackManager.removeListener(this)
    }

    override fun onStateChanged(state: PlaybackManager.State) {
        render(state)
    }

    private fun render(state: PlaybackManager.State) {
        if (!state.hasSong) {
            tvSongTitle.text = "Sin reproducción"
            tvArtist.text = ""
            tvToolbarTitle.text = "REPRODUCIENDO"
            btnPlayPause.setImageResource(R.drawable.ic_play_arrow)

            seekBar.max = 1
            seekBar.progress = 0
            tvCurrentTime.text = "0:00"
            tvTotalTime.text = "0:00"
            ivAlbumArt.setImageResource(R.drawable.mareux)
            return
        }

        tvSongTitle.text = state.title
        tvArtist.text = state.artist
        tvToolbarTitle.text = "REPRODUCIENDO ${state.title} (${state.artist})"

        btnPlayPause.setImageResource(
            if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
        )

        val safeDur = state.durationMs.coerceAtLeast(1)
        seekBar.max = safeDur

        if (!userSeeking) {
            seekBar.progress = state.positionMs.coerceIn(0, safeDur)
        }

        tvCurrentTime.text = formatTime(state.positionMs)
        tvTotalTime.text = formatTime(state.durationMs)

        // ✅ CORRECCIÓN: evitar "Any" (NO usar Elvis con drawable)
        val song = PlaybackManager.getCurrentSong()
        val artUri = song?.albumArtUri

        if (!artUri.isNullOrBlank()) {
            try {
                ivAlbumArt.setImageURI(Uri.parse(artUri))
            } catch (_: Exception) {
                ivAlbumArt.setImageResource(R.drawable.mareux)
            }
        } else {
            ivAlbumArt.setImageResource(R.drawable.mareux)
        }
    }

    private fun formatTime(ms: Int): String {
        val totalSeconds = (ms / 1000).coerceAtLeast(0)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}
