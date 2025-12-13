package com.cleansound.cleansound

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // Variables del mini reproduction
    private lateinit var miniPlayer: CardView
    private lateinit var ivMiniAlbumArt: ImageView
    private lateinit var tvMiniSongTitle: TextView
    private lateinit var tvMiniAlbum: TextView
    private lateinit var tvMiniArtist: TextView
    private lateinit var btnMiniPlayPause: ImageButton
    private lateinit var btnMiniNext: ImageButton
    private lateinit var btnMiniPrevious: ImageButton
    private lateinit var miniSeekBar: SeekBar

    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        MusicPlayerManager.init(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar el mini reproductor
        initMiniPlayer()
        setupMiniPlayerListeners()
        setupPlaylistListener()
    }

    private fun initMiniPlayer() {
        // Obtener referencias a las vistas
        miniPlayer = findViewById(R.id.miniPlayer)
        ivMiniAlbumArt = findViewById(R.id.ivMiniAlbumArt)
        tvMiniSongTitle = findViewById(R.id.tvMiniSongTitle)
        tvMiniAlbum = findViewById(R.id.tvMiniAlbum)
        tvMiniArtist = findViewById(R.id.tvMiniArtist)
        btnMiniPlayPause = findViewById(R.id.btnMiniPlayPause)
        btnMiniNext = findViewById(R.id.btnMiniNext)
        btnMiniPrevious = findViewById(R.id.btnMiniPrevious)
        miniSeekBar = findViewById(R.id.miniSeekBar)

        // Habilitar el efecto de desplazamiento del título si es muy largo
        tvMiniSongTitle.isSelected = true
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupMiniPlayerListeners() {
        // AL HACER CLICK EN EL MINI PLAYER → ABRE LA PANTALLA COMPLETA
        miniPlayer.setOnClickListener {
            abrirReproductorCompleto()
        }

        // También se puede hacer click en el título
        tvMiniSongTitle.setOnClickListener {
            abrirReproductorCompleto()
        }

        ivMiniAlbumArt.setOnClickListener {
            abrirReproductorCompleto()
        }

        // Botón Play/Pause
        btnMiniPlayPause.setOnClickListener {
            togglePlayPause()
        }

        // Botón Siguiente
        btnMiniNext.setOnClickListener {
            siguienteCancion()
        }

        // Botón Anterior
        btnMiniPrevious.setOnClickListener {
            anteriorCancion()
        }

        // Deshabilitar interacción con la SeekBar (solo visual)
        miniSeekBar.setOnTouchListener { _, _ -> true }
    }

    private fun setupPlaylistListener() {
        val tvPlaylists = findViewById<TextView>(R.id.tVPlaylists)

        tvPlaylists.setOnClickListener {
            val intent = Intent(this, PlaylistActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    // FUNCIÓN PRINCIPAL: Abrir la pantalla completa de reproducción
    private fun abrirReproductorCompleto() {
        val intent = Intent(this, NowPlayingActivity::class.java).apply {
            // Pasar datos de la canción actual
            putExtra("SONG_TITLE", tvMiniSongTitle.text.toString())
            putExtra("ALBUM_NAME", tvMiniAlbum.text.toString())
            putExtra("ARTIST_NAME", tvMiniArtist.text.toString())
        }
        startActivity(intent)

        // Animación de transición suave
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    // Cambiar entre Play y Pause
    private fun togglePlayPause() {
        isPlaying = !isPlaying

        if (isPlaying) {
            btnMiniPlayPause.setImageResource(R.drawable.ic_pause)
            MusicPlayerManager.play() // <-- Reproducir la música
        } else {
            btnMiniPlayPause.setImageResource(R.drawable.ic_play_arrow)
            MusicPlayerManager.pause() // <-- Pausar la música
        }
    }

    // Siguiente canción
    private fun siguienteCancion() {
        // Por ahora solo cambiamos la información visual
        tvMiniSongTitle.text = "Siguiente Canción"
        tvMiniAlbum.text = "Álbum Ejemplo"
        tvMiniArtist.text = "Artista Ejemplo"
        // Aquí irá la lógica para cambiar de canción real
    }

    // Canción anterior
    private fun anteriorCancion() {
        // Por ahora solo cambiamos la información visual
        tvMiniSongTitle.text = "Canción Anterior"
        tvMiniAlbum.text = "Álbum Ejemplo"
        tvMiniArtist.text = "Artista Ejemplo"
        // Aquí irá la lógica para cambiar de canción real
    }
}