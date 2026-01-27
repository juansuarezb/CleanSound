package com.cleansound.cleansound.controller

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cleansound.cleansound.R
import com.google.firebase.auth.FirebaseAuth
import model.MediaStoreHelper
import model.Song
import model.PlaylistDoc
import service.PlaylistService

class MainActivity : AppCompatActivity() {

    private lateinit var rvPlaylists: RecyclerView
    private lateinit var rvBiblioteca: RecyclerView

    private lateinit var mediaStoreHelper: MediaStoreHelper
    private val songs = mutableListOf<Song>()
    private lateinit var songAdapter: SongAdapter

    lateinit var textViewBiblioteca: TextView
    lateinit var textViewPlaylists: TextView
    lateinit var imageButtonMenu: ImageButton

    private val playlists = mutableListOf<PlaylistDoc>()
    private lateinit var playlistAdapter: PlaylistAdapter
    private val playlistService = PlaylistService()
    private var playlistListener: com.google.firebase.firestore.ListenerRegistration? = null

    private lateinit var miniPlayerController: MiniPlayerController

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cargarCanciones()
        } else {
            Toast.makeText(this, "Permiso denegado. No se pueden cargar las canciones.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaStoreHelper = MediaStoreHelper(this)

        textViewBiblioteca = findViewById(R.id.tVBiblioteca)
        textViewPlaylists = findViewById(R.id.tVPlaylists)
        imageButtonMenu = findViewById(R.id.iBMenuHamburguesa)

        rvPlaylists = findViewById(R.id.rvPlaylists)
        rvBiblioteca = findViewById(R.id.rvBiblioteca)

        // MiniPlayer (funciona para esta Activity)
        miniPlayerController = MiniPlayerController(this)
        miniPlayerController.bind()

        setupPlaylistsRecyclerView()

        imageButtonMenu.setOnClickListener { showPopupMenu(it) }

        rvBiblioteca.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        songAdapter = SongAdapter(songs) { song ->
            onSongClick(song)
        }
        rvBiblioteca.adapter = songAdapter

        checkAndRequestPermission()

        textViewBiblioteca.setOnClickListener {
            startActivity(Intent(this, LibraryActivity::class.java))
        }

        textViewPlaylists.setOnClickListener {
            startActivity(Intent(this, PlaylistActivity::class.java))
        }
    }

    private fun cargarCanciones() {
        val nuevasCanciones = mediaStoreHelper.getAllSongs()
        songs.clear()
        songs.addAll(nuevasCanciones)
        songAdapter.notifyDataSetChanged()
    }

    private fun setupPlaylistsRecyclerView() {
        rvPlaylists.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        playlistAdapter = PlaylistAdapter(playlists) { playlist ->
            val intent = Intent(this, PlaylistActivity::class.java)
            intent.putExtra("PLAYLIST_ID", playlist.id)
            startActivity(intent)
        }

        rvPlaylists.adapter = playlistAdapter
    }

    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                cargarCanciones()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                Toast.makeText(this, "Necesitamos acceso a tu música para reproducirla", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(permission)
            }
            else -> requestPermissionLauncher.launch(permission)
        }
    }

    private fun onSongClick(song: Song) {
        val index = songs.indexOfFirst { it.uri == song.uri }.coerceAtLeast(0)
        PlaybackManager.setQueue(songs.toList(), index, this)
        startActivity(Intent(this, NowPlayingActivity::class.java))
    }

    private fun showPopupMenu(anchor: View) {
        val popupMenu = PopupMenu(this, anchor)
        popupMenu.menuInflater.inflate(R.menu.menu_main, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_home -> true
                R.id.action_library -> {
                    startActivity(Intent(this, LibraryActivity::class.java))
                    true
                }
                R.id.action_profile -> {
                    Toast.makeText(this, "Perfil (pendiente)", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_salir -> {
                    cerrarSesion()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    override fun onStart() {
        super.onStart()

        // MiniPlayer se suscribe al estado global
        miniPlayerController.start()

        playlistListener?.remove()
        playlistListener = playlistService.listenUserPlaylists(
            onUpdate = { list ->
                playlists.clear()
                playlists.addAll(list)
                playlistAdapter.notifyDataSetChanged()
            },
            onError = { e ->
                Toast.makeText(this, e.message ?: "Error cargando playlists", Toast.LENGTH_LONG).show()
            }
        )
    }

    override fun onStop() {
        super.onStop()
        miniPlayerController.stop()

        playlistListener?.remove()
        playlistListener = null
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
