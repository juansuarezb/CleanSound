package com.cleansound.cleansound

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class PlaylistActivity : AppCompatActivity() {

    private lateinit var ivMenu: ImageView
    private lateinit var bottomPlayer: LinearLayout
    private lateinit var ivPlaylist1: ImageView
    private lateinit var ivPlaylist2: ImageView
    private lateinit var ivPlaylist3: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        ivMenu = findViewById(R.id.ivMenu)
        bottomPlayer = findViewById(R.id.bottomPlayer)
        ivPlaylist1 = findViewById(R.id.ivPlaylist1)
        ivPlaylist2 = findViewById(R.id.ivPlaylist2)
        ivPlaylist3 = findViewById(R.id.ivPlaylist3)
    }

    private fun setupListeners() {
        // Botón de menú para volver
        ivMenu.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        // Click en el mini reproductor (bottomPlayer) para abrir pantalla completa
        bottomPlayer.setOnClickListener {
            abrirReproductorCompleto()
        }

        // Click en cada playlist (opcional - puedes agregar funcionalidad después)
        ivPlaylist1.setOnClickListener {
            // Abrir canciones de la playlist "Clásicos"
            // Por ahora no hace nada, pero puedes implementarlo después
        }

        ivPlaylist2.setOnClickListener {
            // Abrir canciones de la playlist "Rock"
        }

        ivPlaylist3.setOnClickListener {
            // Abrir canciones de la playlist "Rap"
        }
    }

    private fun abrirReproductorCompleto() {
        val intent = Intent(this, NowPlayingActivity::class.java).apply {
            putExtra("SONG_TITLE", "Diosa")
            putExtra("ALBUM_NAME", "Lovers From The Past")
            putExtra("ARTIST_NAME", "Mareux")
        }
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}