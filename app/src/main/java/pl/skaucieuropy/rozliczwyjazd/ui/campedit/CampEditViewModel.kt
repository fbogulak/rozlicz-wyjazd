package pl.skaucieuropy.rozliczwyjazd.ui.campedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import pl.skaucieuropy.rozliczwyjazd.utils.ToastMessage

class CampEditViewModel(private val repository: ReckoningRepository) : ViewModel() {

    val camp = MutableLiveData(Camp.empty())
    var originalCamp = Camp.empty()
    var campHasLoadedFromDb = false

    private val _setupDatePicker = MutableLiveData<Boolean?>()
    val setupDatePicker: LiveData<Boolean?>
        get() = _setupDatePicker

    private val _navigateToCamps = MutableLiveData<Boolean?>()
    val navigateToCamps: LiveData<Boolean?>
        get() = _navigateToCamps

    private val _showToast = MutableLiveData<ToastMessage<*>?>()
    val showToast: LiveData<ToastMessage<*>?>
        get() = _showToast

    fun setupDatePicker() {
        _setupDatePicker.value = true
    }

    fun setupDatePickerCompleted() {
        _setupDatePicker.value = null
    }

    fun navigateToCamps() {
        _navigateToCamps.value = true
    }

    fun navigateToCampsCompleted() {
        _navigateToCamps.value = null
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

    fun getCampFromDb() {
        camp.value?.id?.value?.let {
            viewModelScope.launch {
                originalCamp = repository.getCampById(it)
                camp.value = originalCamp
                campHasLoadedFromDb = true
                setupDatePicker()
            }
        }
    }

    fun saveCamp() {
        val currentCamp = camp.value
        if (currentCamp != null) {
            viewModelScope.launch {
                val result = if (currentCamp.id.value == 0L) {
                    repository.insertCamp(currentCamp)
                } else {
                    repository.updateCamp(currentCamp)
                }
                result.onSuccess {
                    showToast(it)
                }
                result.onFailure {
                    val message = it.message
                    if (message != null) {
                        showToast(message)
                    } else {
                        showToast(R.string.error_saving_camp)
                    }
                }
                navigateToCamps()
            }
        } else {
            showToast(R.string.error_saving_camp)
        }
    }

    fun deleteCamp() {
        val currentCamp = camp.value
        if (currentCamp != null) {
            viewModelScope.launch {
                val result = if (currentCamp.id.value != 0L) {
                    repository.deleteCamp(currentCamp)
                } else {
                    Result.failure(Throwable(ReckoningRepository.ERROR_DELETING_CAMP))
                }
                result.onSuccess {
                    showToast(it)
                }
                result.onFailure {
                    val message = it.message
                    if (message != null) {
                        showToast(message)
                    } else {
                        showToast(R.string.error_deleting_camp)
                    }
                }
                navigateToCamps()
            }
        } else {
            showToast(R.string.error_deleting_camp)
        }
    }
}