package duylt.mobile.app.ecommerce.virgomusic.presentation.adapter.rcv

import android.annotation.SuppressLint
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.domain.model.Timer

class TimerAdapter: BaseQuickAdapter<Timer, BaseViewHolder>(R.layout.item_timer) {

    var indexSelected: Int = 0
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convert(holder: BaseViewHolder, item: Timer) {
        holder.setText(R.id.textTimer, item.title)
        holder.getView<ImageView>(R.id.radioCheck).isActivated = (item.index == indexSelected)
    }
}