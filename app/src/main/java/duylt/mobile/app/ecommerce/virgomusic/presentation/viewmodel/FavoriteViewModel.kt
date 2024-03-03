package duylt.mobile.app.ecommerce.virgomusic.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import duylt.mobile.app.ecommerce.virgomusic.domain.model.FavoriteSong
import duylt.mobile.app.ecommerce.virgomusic.domain.repository.FavoriteRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteViewModel(
    private val repo: FavoriteRepo
) : ViewModel() {

    val listFavorite = MutableLiveData<MutableList<FavoriteSong>>()
    val isLoading = MutableLiveData<Boolean>()

    init {
        getAllFavorite()
    }

    private fun getAllFavorite() =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                isLoading.postValue(true)
                val result = repo.getAllSong()
                listFavorite.postValue(result)
                isLoading.postValue(false)
            }
        }

    fun removeFavorite(s: FavoriteSong) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                isLoading.postValue(true)
                repo.delete(s)
                isLoading.postValue(false)
            }
        }

    fun insertFavorite(s: FavoriteSong) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                isLoading.postValue(true)
                repo.insert(s)
                isLoading.postValue(false)
            }
        }

}