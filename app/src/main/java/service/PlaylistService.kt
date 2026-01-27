package service

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import model.PlaylistDoc
import model.SongRef

class PlaylistService(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    private fun requireUid(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

    fun createPlaylist(
        name: String,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val uid = requireUid()
        val data = hashMapOf(
            "name" to name.trim(),
            "userId" to uid,
            "songs" to emptyList<Map<String, Any>>(),
            "createdAt" to Timestamp.now()
        )

        db.collection("playlists")
            .add(data)
            .addOnSuccessListener { doc -> onSuccess(doc.id) }
            .addOnFailureListener { e -> onError(e) }
    }

    fun listenUserPlaylists(
        onUpdate: (List<PlaylistDoc>) -> Unit,
        onError: (Exception) -> Unit
    ) = db.collection("playlists")
        .whereEqualTo("userId", requireUid())
        .addSnapshotListener { snapshot, e ->
            if (e != null) {
                onError(e)
                return@addSnapshotListener
            }
            val docs = snapshot?.documents.orEmpty().map { d ->
                val name = d.getString("name") ?: ""
                val userId = d.getString("userId") ?: ""
                val createdAt = d.getTimestamp("createdAt")
                val songsRaw = d.get("songs") as? List<Map<String, Any>> ?: emptyList()
                val songs = songsRaw.map {
                    SongRef(
                        id = it["id"] as? String ?: "",
                        title = it["title"] as? String ?: "",
                        artist = it["artist"] as? String ?: "",
                        uri = it["uri"] as? String ?: ""
                    )
                }
                PlaylistDoc(
                    id = d.id,
                    name = name,
                    userId = userId,
                    songs = songs,
                    createdAt = createdAt
                )
            }
            onUpdate(docs)
        }

    fun addSongToPlaylist(
        playlistId: String,
        song: SongRef,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val ref = db.collection("playlists").document(playlistId)

        db.runTransaction { tx ->
            val snap = tx.get(ref)
            val userId = snap.getString("userId") ?: ""
            if (userId != requireUid()) throw IllegalAccessException("No autorizado")

            val songsRaw = snap.get("songs") as? List<Map<String, Any>> ?: emptyList()

            val alreadyExists = songsRaw.any { (it["id"] as? String) == song.id }
            if (!alreadyExists) {
                val newSongs = songsRaw + mapOf(
                    "id" to song.id,
                    "title" to song.title,
                    "artist" to song.artist,
                    "uri" to song.uri
                )
                tx.update(ref, "songs", newSongs)
            }
        }.addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    fun getPlaylistById(
        playlistId: String,
        onSuccess: (PlaylistDoc) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("playlists").document(playlistId)
            .get()
            .addOnSuccessListener { d ->
                if (!d.exists()) {
                    onError(IllegalStateException("Playlist no existe"))
                    return@addOnSuccessListener
                }

                val name = d.getString("name") ?: ""
                val userId = d.getString("userId") ?: ""
                val createdAt = d.getTimestamp("createdAt")
                val songsRaw = d.get("songs") as? List<Map<String, Any>> ?: emptyList()
                val songs = songsRaw.map {
                    SongRef(
                        id = it["id"] as? String ?: "",
                        title = it["title"] as? String ?: "",
                        artist = it["artist"] as? String ?: "",
                        uri = it["uri"] as? String ?: ""
                    )
                }

                if (userId != requireUid()) {
                    onError(IllegalAccessException("No autorizado"))
                    return@addOnSuccessListener
                }

                onSuccess(
                    PlaylistDoc(
                        id = d.id,
                        name = name,
                        userId = userId,
                        songs = songs,
                        createdAt = createdAt
                    )
                )
            }
            .addOnFailureListener { e -> onError(e) }
    }
}
