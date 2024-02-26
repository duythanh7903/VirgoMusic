package duylt.mobile.app.ecommerce.virgomusic.presentation.adapter.rcv

import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.domain.model.Song

class SongVerticalAdapter: BaseQuickAdapter<Song, BaseViewHolder>(R.layout.item_song_vertical) {
    override fun convert(holder: BaseViewHolder, item: Song) {
        holder.setText(R.id.textAuthorName, item.artist)
            .setText(R.id.textSongName, item.title)
            .setText(R.id.textTotalTime, item.formatDuration(item.duration))

        holder.getView<TextView>(R.id.textSongName).isSelected = true
        Glide.with(context).load(item.artUri)
            .apply(RequestOptions().placeholder(R.mipmap.ic_launcher))
            .into(holder.getView(R.id.songImage))
    }
}