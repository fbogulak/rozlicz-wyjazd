package pl.skaucieuropy.rozliczwyjazd.ui.campedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.models.domain.Camp
import pl.skaucieuropy.rozliczwyjazd.repository.BaseRepository
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import pl.skaucieuropy.rozliczwyjazd.ui.base.BaseViewModel
import pl.skaucieuropy.rozliczwyjazd.ui.base.NavigationCommand

class CampEditViewModel(private val repository: BaseRepository) : BaseViewModel() {

    var camp = Camp.empty()
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
        viewModelScope.launch {
            originalCamp = repository.getCampById(camp.id)
            camp = originalCamp
            campHasLoadedFromDb = true
            setupDatePicker()
        }
    }

    fun saveCamp() {
        viewModelScope.launch {
            val result = if (camp.id == 0L) {
                repository.insertCamp(camp)
            } else {
                repository.updateCamp(camp)
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
    }

    fun deleteCamp() {
        viewModelScope.launch {
            val result = if (camp.id != 0L) {
                repository.deleteCamp(camp)
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
    }

    fun navigateToCamps() {
        hideKeyboard.call()
        navigationCommand.value =
            NavigationCommand.To(CampEditFragmentDirections.actionCampEditFragmentToCampsFragment())
    }
}