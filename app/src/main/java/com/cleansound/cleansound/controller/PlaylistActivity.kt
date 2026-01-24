package com.cleansound.cleansound.controller

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import com.cleansound.cleansound.R
import model.Playlist

class PlaylistActivity : AppCompatActivity() {

    private lateinit var ivMenu: ImageView
    private lateinit var bottomPlayer: FragmentContainerView
    private lateinit var ivPlaylist1: ImageView
    private lateinit var ivPlaylist2: ImageView
    private lateinit var ivPlaylist3: ImageView
    private lateinit var playlist: Playlist
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)
        // Aquí recuperas la playlist que enviaste desde MainActivity
        playlist = intent.getSerializableExtra("playlist") as? Playlist
            ?: run {
                // Si no hay playlist, cierra el activity
                finish()
                return
            }
        // Ahora puedes usar los datos de la playlist
        title = playlist.name
        // TODO: Cargar las canciones de esta playlist
        // TODO: Mostrar el cover, número de canciones, etc.
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.bottomPlayer, MiniPlayerFragment())
                .commit()
        }

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

    private fun abrirReproductorCompleto() {
        val intent = Intent(this, NowPlayingActivity::class.java)

        // Datos de prueba (luego los puedes hacer dinámicos)
        intent.putExtra("SONG_TITLE", "Diosa")
        intent.putExtra("ARTIST_NAME", "Mareux")
        intent.putExtra("ALBUM_NAME", "Lovers From The Past")

        startActivity(intent)
        overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }
    private fun setupListeners() {
        ivMenu.setOnClickListener {
            finish()
        }

        bottomPlayer.setOnClickListener {
            abrirReproductorCompleto()
        }
    }
}