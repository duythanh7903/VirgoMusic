package duylt.mobile.app.ecommerce.virgomusic.presentation.base

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import duylt.mobile.app.ecommerce.virgomusic.R

abstract class BaseDialog<DB : ViewDataBinding>(activity: Activity, var canAble: Boolean) :
    Dialog(activity) {

    lateinit var binding: DB

    abstract fun getContentView(): Int
    abstract fun setLanguage()
    abstract fun initView()
    abstract fun bindView()
    abstract fun setListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLanguage()
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding =
            DataBindingUtil.inflate(LayoutInflater.from(context), getContentView(), null, false)
        setContentView(binding.root)
        setCancelable(canAble)
        initView()
        bindView()
        setListener()
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus){
            fullScreenImmersive(window)
        }
    }

    open fun fullScreenImmersive(window: Window?) {
        if (window != null) {
            fullScreenImmersive(window.decorView)
        }
    }

    open fun fullScreenImmersive(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val uiOptions: Int = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
            view.systemUiVisibility = uiOptions
        }
    }

}