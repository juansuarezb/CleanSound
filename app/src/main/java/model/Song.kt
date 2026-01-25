package model

import android.R.attr.duration
import java.io.Serializable
import java.util.Locale

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val uri: String,
    val duration: Long,
    val albumArtUri: String?,
    val dateAdded: Long,
    val size: Long
) : Serializable {

    fun getFormattedDuration(): String {
        val totalSeconds = duration / 1000
        val seconds = totalSeconds % 60
        val minutes = (totalSeconds / 60) % 60
        val hours = totalSeconds / 3600

        return if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }
}