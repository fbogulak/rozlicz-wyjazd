package pl.skaucieuropy.rozliczwyjazd.ui.campedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import pl.skaucieuropy.rozliczwyjazd.repository.BaseRepository
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import pl.skaucieuropy.rozliczwyjazd.ui.base.BaseViewModel
import pl.skaucieuropy.rozliczwyjazd.ui.base.NavigationCommand

class CampEditViewModel(private val repository: BaseRepository) : BaseViewModel() {

    val camp = MutableLiveData(Camp.empty())
    var originalCamp = Camp.empty()
    var campHasLoadedFromDb = false

    private val _setupDatePicker = MutableLiveData<Boolean?>()
    val setupDatePicker: LiveData<Boolean?>
        get() = _setupDatePicker

    fun setupDatePicker() {
        _setupDatePicker.value = true
    }

    fun setupDatePickerCompleted() {
        _setupDatePicker.value = null
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

    fun navigateToCamps() {
        hideKeyboard.call()
        navigationCommand.value =
            NavigationCommand.To(CampEditFragmentDirections.actionCampEditFragmentToCampsFragment())
    }
}