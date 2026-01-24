package model

import java.io.Serializable

data class Song (
    val path: String,
    val tittle: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val dateAdded: Long
) : Serializable