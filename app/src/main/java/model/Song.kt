package model

import java.io.Serializable

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val coverImage: Int
) : Serializable