package com.cleansound.cleansound.controller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import service.MusicPlayerManager
import com.cleansound.cleansound.controller.NowPlayingActivity
import com.cleansound.cleansound.R

class MiniPlayerFragment : Fragment(R.layout.fragment_mini_player) {
    private lateinit var miniPlayer: CardView
    private lateinit var btnMiniPlayPause: ImageButton
    private lateinit var ivMiniAlbumArt: ImageView
    private lateinit var tvMiniSongTitle: TextView
    private lateinit var tvMiniArtist: TextView

    private var isPlaying = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        miniPlayer = view.findViewById(R.id.miniPlayer) // CardView raíz
        btnMiniPlayPause = view.findViewById(R.id.btnMiniPlayPause)
        ivMiniAlbumArt = view.findViewById(R.id.ivMiniAlbumArt)
        tvMiniSongTitle = view.findViewById(R.id.tvMiniSongTitle)
        tvMiniArtist = view.findViewById(R.id.tvMiniArtist)
        miniPlayer.setOnClickListener { abrirReproductorCompleto() }
        tvMiniSongTitle.setOnClickListener { abrirReproductorCompleto() }
        ivMiniAlbumArt.setOnClickListener { abrirReproductorCompleto() }

        btnMiniPlayPause.setOnClickListener { togglePlayPause() }
    }

    private fun abrirReproductorCompleto() {
        val intent = Intent(requireContext(), NowPlayingActivity::class.java)
        startActivity(intent)
        requireActivity().overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }

    private fun togglePlayPause() {
        isPlaying = !isPlaying
        if (isPlaying) {
            btnMiniPlayPause.setImageResource(R.drawable.ic_pause)
            MusicPlayerManager.play()
        } else {
            btnMiniPlayPause.setImageResource(R.drawable.ic_play_arrow)
            MusicPlayerManager.pause()
        }
    }
}