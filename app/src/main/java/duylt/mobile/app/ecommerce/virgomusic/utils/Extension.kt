package duylt.mobile.app.ecommerce.virgomusic.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

var toast: Toast? = null
fun Context.showToast(message: String) {
    toast?.cancel()
    toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
    toast!!.show()
}

fun Context.showToast(message: Int) {
    toast?.cancel()
    toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
    toast!!.show()
}

@SuppressLint("ClickableViewAccessibility")
private fun View.setOnClickAffect(scaleDiff: Float = 0.015f, listener: View.OnClickListener) {
    this.setOnTouchListener { v, motionEvent ->
        when (motionEvent?.action) {
            MotionEvent.ACTION_DOWN -> {
                v?.scaleX = 1 - scaleDiff
                v?.scaleY = 1 - scaleDiff
            }

            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                v?.scaleX = 1f
                v?.scaleY = 1f
            }
        }
        false
    }
    this.setOnClickListener(listener)
}

fun View.setClickAffect(onClick: ((View?) -> Unit)) {
    val listener = SafeOnClickListener()
    listener.onSafeClick = onClick
    this.setOnClickAffect(0.015f, listener)
}

fun View.setClickAffect2(onClick: ((View?) -> Unit)) {
    val listener = SafeOnClickListener()
    listener.onSafeClick = onClick
    this.setOnClickAffect(0.04f, listener)
}

fun View.setClickListener(onClick: ((View?) -> Unit)? = null) {
    val listener = SafeOnClickListener()
    listener.onSafeClick = onClick
    this.setOnClickListener(listener)
}

fun Activity.hideKeyboard() {
    val inputMethodManager =
        this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
}

fun Activity.showKeyboard(editText: View) {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
}

fun View?.show() {
    this?.visibility = View.VISIBLE
}

fun View?.hide() {
    this?.visibility = View.GONE
}

fun View?.invisible() {
    this?.visibility = View.INVISIBLE
}

fun Context.hasNetworkConnection(): Boolean {
    var haveConnectedWifi = false
    var haveConnectedMobile = false
    val cm =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = cm.allNetworkInfo
    for (ni in netInfo) {
        if (ni.typeName.equals("WIFI", ignoreCase = true))
            if (ni.isConnected) haveConnectedWifi = true
        if (ni.typeName.equals("MOBILE", ignoreCase = true))
            if (ni.isConnected) haveConnectedMobile = true
    }
    return haveConnectedWifi || haveConnectedMobile
}