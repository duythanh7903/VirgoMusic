package duylt.mobile.app.ecommerce.virgomusic.utils

import android.os.SystemClock
import android.view.View

open class SafeOnClickListener : View.OnClickListener {
    companion object {
        private var lastClickTime = 0L
    }

    var onSafeClick: ((View?) -> Unit)? = null

    override fun onClick(p0: View?) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 500) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()
        onSafeClick?.invoke(p0)

    }
}