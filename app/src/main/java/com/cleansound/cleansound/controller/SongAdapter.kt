package com.cleansound.cleansound.controller

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cleansound.cleansound.R
import model.Song

class SongAdapter(
    private val songs: List<Song>,
    private val onSongClick: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAlbumArt: ImageView = itemView.findViewById(R.id.ivAlbumArt)
        val tvSongTitle: TextView = itemView.findViewById(R.id.tvSongTitle)
        val tvSongArtist: TextView = itemView.findViewById(R.id.tvSongArtist)
        val tvSongDuration: TextView = itemView.findViewById(R.id.tvSongDuration)

        fun bind(song: Song) {
            tvSongTitle.text = song.title
            tvSongArtist.text = song.artist
            tvSongDuration.text = song.getFormattedDuration()

            // Intentar cargar el artwork del álbum
            try {
                if (song.albumArtUri != null) {
                    val uri = Uri.parse(song.albumArtUri)
                    ivAlbumArt.setImageURI(uri)
                } else {
                    ivAlbumArt.setImageResource(R.drawable.ic_music_note)
                }
            } catch (e: Exception) {
                ivAlbumArt.setImageResource(R.drawable.ic_music_note)
            }

            itemView.setOnClickListener {
                onSongClick(song)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]

        holder.tvSongTitle.text = song.title
        holder.tvSongArtist.text = song.artist
        holder.tvSongDuration.text = song.getFormattedDuration()

// Imagen por defecto (más adelante MediaStore)
        holder.ivAlbumArt.setImageResource(R.drawable.ic_music_note)
    }

    override fun getItemCount(): Int = songs.size
}
