package model
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore

class MediaStoreHelper(private val context: Context) {

    /**
     * Obtiene las primeras 3 canciones del dispositivo
     */
    fun getFirstThreeSongs(): List<Song> {
        val songs = mutableListOf<Song>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.SIZE
        )

        // Solo archivos de música
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        try {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )

            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

                var count = 0
                while (it.moveToNext() && count < 3) {
                    val id = it.getLong(idColumn)
                    val title = it.getString(titleColumn) ?: "Desconocido"
                    val artist = it.getString(artistColumn) ?: "Artista Desconocido"
                    val album = it.getString(albumColumn) ?: "Álbum Desconocido"
                    val duration = it.getLong(durationColumn)
                    val data = it.getString(dataColumn)
                    val albumId = it.getLong(albumIdColumn)
                    val dateAdded = it.getLong(dateAddedColumn)
                    val size = it.getLong(sizeColumn)

                    // Crear URI del archivo de audio
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    // Crear URI del artwork del álbum
                    val albumArtUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    ).toString()

                    val song = Song(
                        id = id.toString(),
                        title = title,
                        artist = artist,
                        album = album,
                        uri = contentUri.toString(),
                        duration = duration,
                        albumArtUri = albumArtUri,
                        dateAdded = dateAdded,
                        size = size
                    )

                    songs.add(song)
                    count++
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return songs
    }
}