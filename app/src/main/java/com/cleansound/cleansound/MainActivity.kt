package com.cleansound.cleansound

import MiniPlayerFragment
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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