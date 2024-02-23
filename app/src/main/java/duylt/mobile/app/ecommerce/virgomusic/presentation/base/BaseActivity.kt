package duylt.mobile.app.ecommerce.virgomusic.presentation.base

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import duylt.mobile.app.ecommerce.virgomusic.data.source.local.datastore.SharePrefUtils
import duylt.mobile.app.ecommerce.virgomusic.presentation.ui.PermissionActivity
import java.util.Locale

abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {
    lateinit var binding: T

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ContextWrapper(newBase?.setAppLocale(SharePrefUtils.language)))
    }

    private fun Context.setAppLocale(language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return createConfigurationContext(config)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        hideSystemNavigationBar()
        super.onCreate(savedInstanceState)
        binding = inflateViewBinding()
        setContentView(binding.root)
        initView()
        setUpListener()
    }

    abstract fun inflateViewBinding(): T

    abstract fun initView()

    abstract fun setUpListener()

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        hideSystemNavigationBar()
        super.onWindowFocusChanged(hasFocus)
    }

    private fun hideSystemNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    fun setColorStatusBar(color: Int) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }

    fun checkPermissionDined() {
        val isPermissionDenied =
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        if (isPermissionDenied) {
            val intent = Intent(this, PermissionActivity::class.java)
            startActivity(intent)
        }
    }

    fun isPermissionDenied(): Boolean =
        ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_DENIED
}