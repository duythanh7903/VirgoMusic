package duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.data.source.local.datastore.SharePrefUtils
import duylt.mobile.app.ecommerce.virgomusic.databinding.ActivityIntroBinding
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseActivity
import duylt.mobile.app.ecommerce.virgomusic.presentation.ui.PermissionActivity
import duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.home.HomeActivity
import duylt.mobile.app.ecommerce.virgomusic.presentation.ui.fra.IntroFragment
import duylt.mobile.app.ecommerce.virgomusic.utils.setClickAffect2

class IntroActivity : BaseActivity<ActivityIntroBinding>() {

    private lateinit var listIndicator: MutableList<ImageView>

    override fun inflateViewBinding(): ActivityIntroBinding =
        ActivityIntroBinding.inflate(layoutInflater)

    override fun initView() {
        setStatusBarGradiant()

        listIndicator = mutableListOf()

        binding.apply {
            viewPager.apply {
                adapter = MyViewPagerAdapter()
                registerOnPageChangeCallback(viewPagerListener)
                isUserInputEnabled = false
            }

            // add indicator to list
            listIndicator.add(dot1)
            listIndicator.add(dot2)
            listIndicator.add(dot3)
        }

        selectIndicator(0)
    }

    override fun setUpListener() {
        binding.apply {
            textNext.setClickAffect2 {
                val current: Int = binding.viewPager.currentItem
                if (current >= 2) {
                    goNextScreen()
                } else {
                    binding.viewPager.currentItem = current + 1
                }
            }
        }
    }

    override fun onDestroy() {
        binding.viewPager.unregisterOnPageChangeCallback(viewPagerListener)
        super.onDestroy()
    }

    private var viewPagerListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) { selectIndicator(position) }
        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) = Unit
        override fun onPageScrollStateChanged(arg0: Int) = Unit
    }

    inner class MyViewPagerAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            val bundle = Bundle()
            bundle.putInt("page", position)
            return IntroFragment().apply { arguments = bundle }
        }
    }

    private fun selectIndicator(indexIndicator: Int) {
        for (item in listIndicator) {
            item.setImageResource(R.drawable.ic_dot_not_select)
        }
        listIndicator[indexIndicator].setImageResource(R.drawable.ic_dot_select)
        binding.textNext.text =
            when (indexIndicator) {
                2 -> resources.getString(R.string.started)
                else -> resources.getString(R.string.next)
            }
    }

    private fun goNextScreen() {
        val intent = Intent(this,
            if (isPermissionDenied()) PermissionActivity::class.java else HomeActivity::class.java
        )
        intent.putExtra("from_intro", true)
        startActivity(intent)
        finish()
    }
}