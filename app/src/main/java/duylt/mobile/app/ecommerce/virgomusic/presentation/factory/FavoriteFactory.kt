package duylt.mobile.app.ecommerce.virgomusic.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import duylt.mobile.app.ecommerce.virgomusic.domain.repository.FavoriteRepo
import duylt.mobile.app.ecommerce.virgomusic.presentation.viewmodel.FavoriteViewModel

class FavoriteFactory(
    private val repo: FavoriteRepo
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(repo) as T
        }

        throw IllegalArgumentException("Unknown View Model Class")
    }
}