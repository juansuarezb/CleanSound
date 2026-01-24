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
import model.Song


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
        setupBibliotecaRecyclerView()
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
    private fun setupBibliotecaRecyclerView() {
        val songs = listOf(
            Song("1", "Song 1", "Artist 1", "Album 1", R.mipmap.ic_launcher),
            Song("2", "Song 2", "Artist 2", "Album 2", R.mipmap.ic_launcher),
            Song("3", "Song 3", "Artist 3", "Album 3", R.mipmap.ic_launcher)
        )

        val recyclerView = findViewById<RecyclerView>(R.id.rvBiblioteca)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SongAdapter(songs)
    }
}