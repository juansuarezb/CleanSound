package com.cleansound.cleansound.controller

import com.cleansound.cleansound.controller.MiniPlayerFragment
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import service.MusicPlayerManager
import com.cleansound.cleansound.controller.PlaylistActivity
import com.cleansound.cleansound.R

class MainActivity : AppCompatActivity() {

    private var isPlaying = false
    private lateinit var tvPlaylists: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        MusicPlayerManager.init(this)
        tvPlaylists = findViewById(R.id.tVPlaylists)
        tvPlaylists.setOnClickListener {
            startActivity(Intent(this, PlaylistActivity::class.java))
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.bottomPlayer, MiniPlayerFragment())
                .commit()
        }

    }

}