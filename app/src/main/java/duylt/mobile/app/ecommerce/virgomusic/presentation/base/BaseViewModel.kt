package duylt.mobile.app.ecommerce.virgomusic.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

open class BaseViewModel : ViewModel(), CoroutineScope {
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    /**
     * This function is called when the ViewModel is no longer used and will be destroyed.
     * It cancels the job, which will cancel all the coroutines that were launched in this scope.
     */
    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    /**
     * Extension function for simplified coroutines usage in ViewModel
     */
    fun launchSafely(block: suspend CoroutineScope.() -> Unit) {
        launch {
            try {
                block()
            } catch (e: Exception) {
                // Handle exceptions here, you may want to log or notify the user
                e.printStackTrace()
            }
        }
    }
}
