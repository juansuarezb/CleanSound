package com.cleansound.cleansound.controller

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cleansound.cleansound.R
import model.Playlist


class MainActivity : AppCompatActivity() {

    private var isPlaying = false
    private lateinit var tvPlaylists: TextView
    private lateinit var rvPlaylists: RecyclerView
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        MusicPlayerManager.init(this)

        tvPlaylists = findViewById(R.id.tVPlaylists)
        rvPlaylists = findViewById(R.id.rvPlaylists)

        tvPlaylists.setOnClickListener {
            startActivity(Intent(this, PlaylistActivity::class.java))
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.bottomPlayer, MiniPlayerFragment())
                .commit()
        }

        setupPlaylistsRecyclerView()
    }

    private fun setupPlaylistsRecyclerView() {
        // Datos de ejemplo
        val playlists = listOf(
            Playlist("1", "Mis Favoritas", R.mipmap.ic_launcher, 25),
            Playlist("2", "Rock Clásico", R.mipmap.ic_launcher, 30),
            Playlist("3", "Chill Vibes", R.mipmap.ic_launcher, 18),
            Playlist("4", "Workout", R.mipmap.ic_launcher, 42)
        )

        // Configurar LayoutManager horizontal
        val layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rvPlaylists.layoutManager = layoutManager

        // Configurar Adapter - CORREGIDO
        playlistAdapter = PlaylistAdapter(playlists, ::onPlaylistClick)
        rvPlaylists.adapter = playlistAdapter
    }

    private fun onPlaylistClick(playlist: Playlist) {
        val intent = Intent(this, PlaylistActivity::class.java)
        intent.putExtra("playlist", playlist)
        startActivity(intent)
    }
}