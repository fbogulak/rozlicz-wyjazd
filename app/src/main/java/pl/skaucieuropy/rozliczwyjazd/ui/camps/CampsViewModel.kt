package pl.skaucieuropy.rozliczwyjazd.ui.camps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import pl.skaucieuropy.rozliczwyjazd.utils.ToastMessage

class CampsViewModel(private val repository: ReckoningRepository) : ViewModel() {

    val camps = repository.allCamps

    private val _navigateToCampEdit = MutableLiveData<Boolean?>()
    val navigateToCampEdit: LiveData<Boolean?>
        get() = _navigateToCampEdit

    private val _showToast = MutableLiveData<ToastMessage<*>?>()
    val showToast: LiveData<ToastMessage<*>?>
        get() = _showToast

    fun navigateToCampEdit() {
        _navigateToCampEdit.value = true
    }

    fun navigateToCampEditCompleted() {
        _navigateToCampEdit.value = null
    }

    private fun showToast(message: String) {
        _showToast.value = ToastMessage.from(message)
    }

    private fun showToast(messageResId: Int) {
        _showToast.value = ToastMessage.from(messageResId)
    }

    fun showToastCompleted() {
        _showToast.value = null
    }

    fun changeActiveCamp(campId: Long?) {
        if (campId != null) {
            viewModelScope.launch {
                val activeCampId = repository.getActiveCampId()
                val result = if (campId == activeCampId) {
                    Result.success(R.string.camp_already_active)
                } else {
                    repository.changeActiveCamp(campId)
                }
                result.onSuccess {
                    showToast(it)
                }
                result.onFailure {
                    val message = it.message
                    if (message != null) {
                        showToast(message)
                    } else {
                        showToast(R.string.error_setting_as_active)
                    }
                }
            }
        } else {
            showToast(R.string.error_setting_as_active)
        }
    }
}