package duylt.mobile.app.ecommerce.virgomusic.presentation.adapter.rcv

import android.annotation.SuppressLint
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.data.source.local.datastore.SharePrefUtils
import duylt.mobile.app.ecommerce.virgomusic.domain.model.Language

class LanguageAdapter: BaseQuickAdapter<Language, BaseViewHolder>(R.layout.layout_item_language) {

    var selectedLang: String = SharePrefUtils.language
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convert(holder: BaseViewHolder, item: Language) {
        holder.setText(R.id.tvLanguage, item.name)
            .setImageResource(R.id.ivAvatar, item.flagIcon)
        holder.getView<ImageView>(R.id.checked).isActivated = selectedLang == item.code
    }
}