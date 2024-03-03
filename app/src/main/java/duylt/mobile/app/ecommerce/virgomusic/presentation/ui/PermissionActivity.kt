package duylt.mobile.app.ecommerce.virgomusic.presentation.ui

import android.content.Intent
import com.permissionx.guolindev.PermissionX
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.databinding.ActivityPermissionBinding
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseActivity
import duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.home.HomeActivity
import duylt.mobile.app.ecommerce.virgomusic.utils.setClickAffect2
import duylt.mobile.app.ecommerce.virgomusic.utils.showToast

class PermissionActivity : BaseActivity<ActivityPermissionBinding>() {
    
    private var isFromIntro = false
    
    override fun inflateViewBinding(): ActivityPermissionBinding =
        ActivityPermissionBinding.inflate(layoutInflater)

    override fun initView() {
        isFromIntro = intent.getBooleanExtra("from_intro", false)
    }

    override fun setUpListener() {
        binding.apply {
            buttonWriteStoragePermission.setClickAffect2 { requestPermissionWriteStorage() }

            buttonGo.setClickAffect2 { go() }
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("Unit"))
    override fun onBackPressed() = Unit

    private fun requestPermissionWriteStorage() {
        PermissionX.init(this)
            .permissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request { allGranted, _, _ ->
                binding.buttonWriteStoragePermission.isActivated = allGranted
            }
    }

    private fun go() {
        if (isPermissionDenied()) {
            showToast(resources.getString(R.string.must_accept_all_permission))
        } else { 
            if (isFromIntro) {
                val intent = Intent(this@PermissionActivity, HomeActivity::class.java)
                startActivity(intent)
            }
            finish()
        }
    }
}