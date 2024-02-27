package duylt.mobile.app.ecommerce.virgomusic.presentation.ui.fra

import com.bumptech.glide.Glide
import duylt.mobile.app.ecommerce.virgomusic.R
import duylt.mobile.app.ecommerce.virgomusic.databinding.FragmentIntroBinding
import duylt.mobile.app.ecommerce.virgomusic.presentation.base.BaseFragment

class IntroFragment : BaseFragment<FragmentIntroBinding>() {
    override fun inflateViewBinding(): FragmentIntroBinding =
        FragmentIntroBinding.inflate(layoutInflater)

    override fun initView() {
        val index = arguments?.getInt("page", 0) ?: 0
        Glide.with(requireContext()).load(
            when (index) {
                0 -> R.drawable.image_intro_1
                1 -> R.drawable.image_intro_2
                else -> R.drawable.image_intro_3
            }
        ).into(binding.imageIntro)
        binding.textTitle.text = getString(
            when (index) {
                0 -> R.string.title_intro_1
                1 -> R.string.title_intro_2
                else -> R.string.title_intro_3
            }
        )
        binding.textDes.text = getString(
            when (index) {
                0 -> R.string.des_intro_1
                1 -> R.string.des_intro_2
                else -> R.string.des_intro_3
            }
        )
    }

    override fun setUpListener() = Unit

}