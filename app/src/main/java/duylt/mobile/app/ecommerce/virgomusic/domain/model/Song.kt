package duylt.mobile.app.ecommerce.virgomusic.domain.model

import java.io.Serializable
import java.util.concurrent.TimeUnit

data class Song(
    var id: String = "",
    var title: String = "",
    var album: String = "",
    var artist: String = "",
    var duration: Long = 0,
    var path: String = "",
    var artUri: String = ""
): Serializable {

    fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
                minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)

        return String.format("%02d:%02d", minutes, seconds)
    }

}