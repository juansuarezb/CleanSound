package model

import com.google.firebase.Timestamp

data class PlaylistDoc(
    val id: String = "",          // lo llenamos desde el DocumentSnapshot.id
    val name: String = "",
    val userId: String = "",
    val songs: List<SongRef> = emptyList(),
    val createdAt: Timestamp? = null
)

data class SongRef(
    val id: String = "",          // id del MediaStore (Song.id)
    val title: String = "",
    val artist: String = "",
    val uri: String = ""          // Song.uri
)
