package com.cleansound.cleansound.controller

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cleansound.cleansound.databinding.ActivityLibraryBinding
import model.MediaStoreHelper
import model.Song

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding
    private lateinit var miniPlayerController: MiniPlayerController

    private lateinit var mediaStoreHelper: MediaStoreHelper
    private val songs = mutableListOf<Song>()
    private lateinit var songAdapter: SongAdapter

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
        enableEdgeToEdge()

        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // MiniPlayer
        miniPlayerController = MiniPlayerController(this)
        miniPlayerController.bind()

        // MediaStore
        mediaStoreHelper = MediaStoreHelper(this)

        // RecyclerView
        binding.rvLibrary.layoutManager = LinearLayoutManager(this)
        songAdapter = SongAdapter(songs) { song ->
            onSongClick(song)
        }
        binding.rvLibrary.adapter = songAdapter

        // Back
        binding.btnBack.setOnClickListener { finish() }

        checkAndRequestPermission()
    }

    override fun onStart() {
        super.onStart()
        miniPlayerController.start()
    }

    override fun onStop() {
        super.onStop()
        miniPlayerController.stop()
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
                Toast.makeText(this, "Necesitamos acceso a tu música para mostrarla", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(permission)
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun cargarCanciones() {
        val nuevas = mediaStoreHelper.getAllSongs()
        songs.clear()
        songs.addAll(nuevas)
        songAdapter.notifyDataSetChanged()
    }

    private fun onSongClick(song: Song) {
        val index = songs.indexOfFirst { it.uri == song.uri }.coerceAtLeast(0)
        PlaybackManager.setQueue(songs.toList(), index, this)
        startActivity(Intent(this, NowPlayingActivity::class.java))
    }
}
