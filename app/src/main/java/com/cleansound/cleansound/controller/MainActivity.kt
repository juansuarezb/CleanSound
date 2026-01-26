package com.cleansound.cleansound.controller

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import model.MediaStoreHelper
import model.Song
import com.cleansound.cleansound.controller.SongAdapter
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    // RecyclerViews
    private lateinit var rvPlaylists: RecyclerView
    private lateinit var rvBiblioteca: RecyclerView


    // MediaStore
    private lateinit var mediaStoreHelper: MediaStoreHelper
    private val songs = mutableListOf<Song>()
    private lateinit var songAdapter: SongAdapter

    lateinit var textViewBiblioteca: TextView
    lateinit var imageButtonMenu: ImageButton

    // Launcher para solicitar permisos
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cargarCanciones()
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
        textViewBiblioteca = findViewById(R.id.tVBiblioteca)
        imageButtonMenu = findViewById(R.id.iBMenuHamburguesa)
        // Inicializar RecyclerViews
        rvPlaylists = findViewById(R.id.rvPlaylists)
        rvBiblioteca = findViewById(R.id.rvBiblioteca)

        // Configurar RecyclerView de Playlists
        setupPlaylistsRecyclerView()
        imageButtonMenu.setOnClickListener {
            showPopupMenu(it)
        }

        // Configurar RecyclerView de Biblioteca
        rvBiblioteca.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        songAdapter = SongAdapter(songs) { song ->
            onSongClick(song)
        }
        rvBiblioteca.adapter = songAdapter
        cargarCanciones()
    }

    private fun cargarCanciones() {
        val mediaStoreHelper = MediaStoreHelper(this)
        val nuevasCanciones = mediaStoreHelper.getAllSongs()

        songs.clear()
        songs.addAll(nuevasCanciones)

        songAdapter.notifyDataSetChanged()
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
                cargarCanciones()
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



    private fun onSongClick(song: Song) {
        // Por ahora solo mostramos un Toast
        Toast.makeText(
            this,
            "Seleccionaste: ${song.title}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showPopupMenu(anchor: View) {
        val popupMenu = PopupMenu(this, anchor)
        popupMenu.menuInflater.inflate(R.menu.menu_main, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    true
                }

                R.id.action_library -> {
                    startActivity(Intent(this, LibraryActivity::class.java))
                    true
                }

                R.id.action_profile -> {
                    // TODO: ir a ProfileActivity
                    true
                }

                R.id.action_salir -> {
                    // Cerrar sesión
                    cerrarSesion()
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }
    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


}