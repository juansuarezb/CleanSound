package com.cleansound.cleansound.controller

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.cleansound.cleansound.R
import model.MediaStoreHelper
import model.Song

class PlaylistActivity : AppCompatActivity() {

    private lateinit var ivMenu: ImageView
    private lateinit var ivPlaylist1: ImageView
    private lateinit var ivPlaylist2: ImageView
    private lateinit var ivPlaylist3: ImageView

    private lateinit var miniPlayerController: MiniPlayerController

    private lateinit var mediaStoreHelper: MediaStoreHelper
    private val allSongs = mutableListOf<Song>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        // Cargar canciones del teléfono (MediaStore)
        mediaStoreHelper = MediaStoreHelper(this)
        allSongs.clear()
        allSongs.addAll(mediaStoreHelper.getAllSongs())

        // Conectar MiniPlayer
        miniPlayerController = MiniPlayerController(this)
        miniPlayerController.bind()

        initViews()
        setupListeners()
    }

    private fun initViews() {
        ivMenu = findViewById(R.id.ivMenu)
        ivPlaylist1 = findViewById(R.id.ivPlaylist1)
        ivPlaylist2 = findViewById(R.id.ivPlaylist2)
        ivPlaylist3 = findViewById(R.id.ivPlaylist3)
    }

    private fun setupListeners() {
        // Tu botón menú en esta pantalla lo usas como "volver"
        ivMenu.setOnClickListener { finish() }

        // Al tocar una playlist, reproducimos una cola de ejemplo
        // (aquí lo dejo simple: reproduce desde un índice distinto para simular 3 playlists)

        ivPlaylist1.setOnClickListener {
            playFromIndex(0)
        }

        ivPlaylist2.setOnClickListener {
            playFromIndex(3)
        }

        ivPlaylist3.setOnClickListener {
            playFromIndex(6)
        }
    }

    private fun playFromIndex(startIndex: Int) {
        if (allSongs.isEmpty()) return

        val safeIndex = startIndex.coerceIn(0, allSongs.lastIndex)
        PlaybackManager.setQueue(allSongs.toList(), safeIndex, this)
        startActivity(Intent(this, NowPlayingActivity::class.java))
    }

    override fun onStart() {
        super.onStart()
        miniPlayerController.start()
    }

    override fun onStop() {
        super.onStop()
        miniPlayerController.stop()
    }
}
