package com.cleansound.cleansound.controller

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cleansound.cleansound.R
import model.MediaStoreHelper
import model.Song
import com.cleansound.cleansound.controller.SongAdapter






class MainActivity : AppCompatActivity() {

    // RecyclerViews
    private lateinit var rvPlaylists: RecyclerView
    private lateinit var rvBiblioteca: RecyclerView

    // MediaStore
    private lateinit var mediaStoreHelper: MediaStoreHelper
    private val songs = mutableListOf<Song>()

    // Launcher para solicitar permisos
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadSongs()
        } else {
            Toast.makeText(
                this,
                "Permiso denegado. No se pueden cargar las canciones.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar MediaStore helper
        mediaStoreHelper = MediaStoreHelper(this)

        // Inicializar RecyclerViews
        rvPlaylists = findViewById(R.id.rvPlaylists)
        rvBiblioteca = findViewById(R.id.rvBiblioteca)

        // Configurar RecyclerView de Playlists
        setupPlaylistsRecyclerView()

        // Configurar RecyclerView de Biblioteca
        rvBiblioteca.layoutManager = LinearLayoutManager(this)

        // Verificar y solicitar permisos para cargar canciones
        checkAndRequestPermission()
    }

    private fun setupPlaylistsRecyclerView() {
        // Configuración horizontal para playlists
        rvPlaylists.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        // TODO: Aquí irá tu adapter de playlists cuando lo implementes
    }

    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) ==
                    PackageManager.PERMISSION_GRANTED -> {
                loadSongs()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                Toast.makeText(
                    this,
                    "Necesitamos acceso a tu música para reproducirla",
                    Toast.LENGTH_LONG
                ).show()
                requestPermissionLauncher.launch(permission)
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun loadSongs() {
        // Obtener las 3 primeras canciones
        songs.clear()
        songs.addAll(mediaStoreHelper.getFirstThreeSongs())

        if (songs.isEmpty()) {
            Toast.makeText(
                this,
                "No se encontraron canciones en el dispositivo",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Configurar el adapter de biblioteca
            val adapter = SongAdapter(songs) { song ->
                onSongClick(song)
            }
            rvBiblioteca.adapter = adapter
        }
    }

    private fun onSongClick(song: Song) {
        // Por ahora solo mostramos un Toast
        Toast.makeText(
            this,
            "Seleccionaste: ${song.title}",
            Toast.LENGTH_SHORT
        ).show()
    }
}