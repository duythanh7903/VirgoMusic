package duylt.mobile.app.ecommerce.virgomusic.presentation.adapter.rcv

import android.annotation.SuppressLint
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.domain.model.Preset

class PresetAdapter: BaseQuickAdapter<Preset, BaseViewHolder>(R.layout.item_equalizer_type) {

    var indexSelected: Int = 0
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convert(holder: BaseViewHolder, item: Preset) {
        holder.setText(R.id.textEqualizerType, item.title)
        holder.getView<ImageView>(R.id.radioCheck).isActivated = (item.index == indexSelected)
    }
}