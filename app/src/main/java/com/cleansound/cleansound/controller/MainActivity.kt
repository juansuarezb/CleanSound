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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cleansound.cleansound.R
import com.google.firebase.auth.FirebaseAuth
import model.MediaStoreHelper
import model.PlaylistDoc
import model.Song
import service.PlaylistService

class MainActivity : AppCompatActivity() {

    // UI
    private lateinit var rvPlaylists: RecyclerView
    private lateinit var rvBiblioteca: RecyclerView
    private lateinit var textViewBiblioteca: TextView
    private lateinit var textViewPlaylists: TextView
    private lateinit var imageButtonMenu: ImageButton

    // MediaStore
    private lateinit var mediaStoreHelper: MediaStoreHelper
    private val songs = mutableListOf<Song>()
    private lateinit var songAdapter: SongAdapter

    // Playlists (Firestore)
    private val playlists = mutableListOf<PlaylistDoc>()
    private lateinit var playlistAdapter: PlaylistAdapter
    private val playlistService = PlaylistService()
    private var playlistListener: com.google.firebase.firestore.ListenerRegistration? = null

    // Permisos
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) cargarCanciones()
        else Toast.makeText(
            this,
            "Permiso denegado. No se pueden cargar las canciones.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Views
        mediaStoreHelper = MediaStoreHelper(this)
        textViewBiblioteca = findViewById(R.id.tVBiblioteca)
        textViewPlaylists = findViewById(R.id.tVPlaylists)
        imageButtonMenu = findViewById(R.id.iBMenuHamburguesa)

        rvPlaylists = findViewById(R.id.rvPlaylists)
        rvBiblioteca = findViewById(R.id.rvBiblioteca)

        // Recycler Playlists
        setupPlaylistsRecyclerView()

        // Recycler Biblioteca (songs)
        rvBiblioteca.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        songAdapter = SongAdapter(songs) { song -> onSongClick(song) }
        rvBiblioteca.adapter = songAdapter

        // Menú
        imageButtonMenu.setOnClickListener { showPopupMenu(it) }

        // Navegación por TextViews
        textViewBiblioteca.setOnClickListener {
            startActivity(Intent(this, LibraryActivity::class.java))
        }

        // Si quieres que al tocar “Playlists” abra el diálogo de crear:
        textViewPlaylists.setOnClickListener {
            showCreatePlaylistDialog()
        }
        // Si prefieres que abra otra pantalla, cambia por:
        // startActivity(Intent(this, PlaylistActivity::class.java))

        // Permisos + cargar canciones
        checkAndRequestPermission()
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

    private fun cargarCanciones() {
        val nuevasCanciones = mediaStoreHelper.getAllSongs()
        songs.clear()
        songs.addAll(nuevasCanciones)
        songAdapter.notifyDataSetChanged()
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
        val songUri = try {
            Uri.parse(song.uri)
        } catch (e: Exception) {
            Toast.makeText(this, "URI de canción no válida", Toast.LENGTH_SHORT).show()
            return
        }

        MusicPlayerManager.playSong(this, songUri)

        val intent = Intent(this, NowPlayingActivity::class.java)
        startActivity(intent)
    }

    private fun showCreatePlaylistDialog() {
        val input = android.widget.EditText(this)
        input.hint = "Nombre de la playlist"

        AlertDialog.Builder(this)
            .setTitle("Nueva playlist")
            .setView(input)
            .setPositiveButton("Crear") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isEmpty()) {
                    Toast.makeText(this, "Nombre requerido", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                playlistService.createPlaylist(
                    name = name,
                    onSuccess = {
                        Toast.makeText(this, "Playlist creada", Toast.LENGTH_SHORT).show()
                    },
                    onError = { e ->
                        Toast.makeText(this, e.message ?: "Error", Toast.LENGTH_LONG).show()
                    }
                )
            }
            .setNegativeButton("Cancelar", null)
            .show()
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

                // ✅ NUEVO: crear playlist desde menú
                R.id.action_create_playlist -> {
                    showCreatePlaylistDialog()
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
