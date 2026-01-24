package model

import java.io.Serializable

data class Playlist(
    val id: String,
    val name: String,
    val coverImage: Int, // resource ID de la imagen
    val songCount: Int
) : Serializable