package com.cleansound.cleansound

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class NowPlayingActivity : AppCompatActivity() {

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

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var isFavorite = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_now_playing)

        initViews()
        setupListeners()
        setupMediaPlayer()
        updateSeekBar()
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

        // Obtener datos de la canción desde el Intent
        val songTitle = intent.getStringExtra("SONG_TITLE") ?: "Diosa"
        val artistName = intent.getStringExtra("ARTIST_NAME") ?: "Mareux"
        val songPath = intent.getStringExtra("SONG_PATH")

        tvSongTitle.text = songTitle
        tvArtist.text = artistName
        tvToolbarTitle.text = "REPRODUCIENDO $songTitle ($artistName)"

        // Habilitar marquee para el título si es muy largo
        tvSongTitle.isSelected = true
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnPlayPause.setOnClickListener {
            togglePlayPause()
        }

        btnNext.setOnClickListener {
            playNextSong()
        }

        btnPrevious.setOnClickListener {
            playPreviousSong()
        }

        btnFavorite.setOnClickListener {
            toggleFavorite()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.let {
                        val newPosition = (it.duration * progress) / 100
                        it.seekTo(newPosition)
                        tvCurrentTime.text = formatTime(newPosition)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupMediaPlayer() {
        // Aquí debes inicializar tu MediaPlayer con la canción actual
        val songPath = intent.getStringExtra("SONG_PATH")

        try {
            mediaPlayer = MediaPlayer().apply {
                // Puedes usar setDataSource con la ruta del archivo
                // setDataSource(songPath)

                // O usar un recurso raw para pruebas
                // val afd = resources.openRawResourceFd(R.raw.sample_song)
                // setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                // afd.close()

                prepare()

                setOnCompletionListener {
                    playNextSong()
                }
            }

            // Configurar la duración total
            tvTotalTime.text = formatTime(mediaPlayer?.duration ?: 0)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun togglePlayPause() {
        mediaPlayer?.let {
            if (isPlaying) {
                it.pause()
                btnPlayPause.setImageResource(R.drawable.ic_play_arrow)
            } else {
                it.start()
                btnPlayPause.setImageResource(R.drawable.ic_pause)
            }
            isPlaying = !isPlaying
        }
    }

    private fun toggleFavorite() {
        isFavorite = !isFavorite
        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite)
            btnFavorite.setColorFilter(
                ContextCompat.getColor(this, android.R.color.holo_red_light)
            )
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border)
            btnFavorite.setColorFilter(
                ContextCompat.getColor(this, android.R.color.darker_gray)
            )
        }
        // Aquí puedes guardar el estado en tu base de datos o SharedPreferences
    }

    private fun playNextSong() {
        // Implementa la lógica para reproducir la siguiente canción
        // Esto dependerá de cómo gestiones tu lista de reproducción
    }

    private fun playPreviousSong() {
        // Implementa la lógica para reproducir la canción anterior
        // O reiniciar la canción actual si ya pasaron más de 3 segundos
        mediaPlayer?.let {
            if (it.currentPosition > 3000) {
                it.seekTo(0)
            } else {
                // Reproducir canción anterior de la lista
            }
        }
    }

    private fun updateSeekBar() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                val currentPosition = it.currentPosition
                val duration = it.duration

                seekBar.progress = if (duration > 0) {
                    (currentPosition * 100) / duration
                } else {
                    0
                }

                tvCurrentTime.text = formatTime(currentPosition)
            }
        }

        handler.postDelayed({ updateSeekBar() }, 100)
    }

    private fun formatTime(milliseconds: Int): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
            btnPlayPause.setImageResource(R.drawable.ic_play_arrow)
        }
    }
}