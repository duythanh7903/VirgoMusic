package duylt.mobile.app.ecommerce.virgomusic.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.permissionx.guolindev.PermissionX
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.databinding.ActivityPermissionBinding
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseActivity
import duylt.mobile.app.ecommerce.virgomusic.utils.logger
import duylt.mobile.app.ecommerce.virgomusic.utils.setClickAffect2
import duylt.mobile.app.ecommerce.virgomusic.utils.showToast
import duylt.mobile.app.ecommerce.virgomusic.utils.toast

class PermissionActivity : BaseActivity<ActivityPermissionBinding>() {
    override fun inflateViewBinding(): ActivityPermissionBinding =
        ActivityPermissionBinding.inflate(layoutInflater)

    override fun initView() {
    }

    override fun setUpListener() {
        binding.apply {
            buttonWriteStoragePermission.setClickAffect2 { requestPermissionWriteStorage() }

            buttonGo.setClickAffect2 { go() }
        }
    }

    override fun onBackPressed() {
        logger("Can not back!!!")
    }

    private fun requestPermissionWriteStorage() {
        PermissionX.init(this)
            .permissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request { allGranted, grantedList, deniedList ->
                binding.buttonWriteStoragePermission.isActivated = allGranted
            }
    }

    private fun go() {
        if (isPermissionDenied()) {
            showToast(resources.getString(R.string.must_accept_all_permission))
        } else { finish() }
    }
}