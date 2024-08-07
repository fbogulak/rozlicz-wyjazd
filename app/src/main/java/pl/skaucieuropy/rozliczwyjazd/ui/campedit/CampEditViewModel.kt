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
import java.util.*

class CampEditViewModel(private val repository: BaseRepository) : BaseViewModel() {

    private var _camp = Camp.empty()
    val camp: Camp
        get() = _camp

    private var _campHasLoadedFromDb = false
    val campHasLoadedFromDb: Boolean
        get() = _campHasLoadedFromDb

    val campName = MutableLiveData(camp.name)
    val campBudget = MutableLiveData(camp.budget)
    val campStartDate = MutableLiveData(camp.startDate)
    val campEndDate = MutableLiveData(camp.endDate)

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
            _camp = repository.getCampById(camp.id)
            campName.value = camp.name
            campBudget.value = camp.budget
            campStartDate.value = camp.startDate
            campEndDate.value = camp.endDate
            _campHasLoadedFromDb = true
            setupDatePicker()
        }
    }

    fun saveCamp() {
        viewModelScope.launch {
            updateCampProperties()
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
            updateCampProperties()
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

    private fun updateCampProperties() {
        _camp.name = campName.value ?: ""
        _camp.budget = campBudget.value ?: 0.0
        _camp.startDate = campStartDate.value ?: Date()
        _camp.endDate = campEndDate.value ?: Date()
    }

    fun navigateToCamps() {
        hideKeyboard.value = true
        navigationCommand.value =
            NavigationCommand.To(CampEditFragmentDirections.actionCampEditFragmentToCampsFragment())
    }
}