package duylt.mobile.app.ecommerce.virgomusic.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import duylt.mobile.app.ecommerce.virgomusic.presentation.viewmodel.HomeViewModel

class HomeFactory(): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel() as T
        }

        throw IllegalArgumentException("Unknown View Model Class")
    }
}