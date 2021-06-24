package pl.skaucieuropy.rozliczwyjazd.ui.campedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository

class CampEditViewModel(private val repository: ReckoningRepository) : ViewModel() {

    val camp = MutableLiveData(Camp.empty())
    var originalCamp = Camp.empty()
    var campHasLoadedFromDb = false

    private val _navigateToCamps = MutableLiveData<Boolean?>()
    val navigateToCamps: LiveData<Boolean?>
        get() = _navigateToCamps

    fun navigateToCamps() {
        _navigateToCamps.value = true
    }

    fun navigateToCampsCompleted() {
        _navigateToCamps.value = null
    }

    fun getCampFromDb() {
        camp.value?.id?.value?.let {
            viewModelScope.launch {
                originalCamp = repository.getCampById(it)
                camp.value = originalCamp
                campHasLoadedFromDb = true
            }
        }
    }

    fun saveCamp() {
        camp.value?.let {
            viewModelScope.launch {
                if (it.id.value == 0L) {
                    repository.insertCamp(it)
                } else {
                    repository.updateCamp(it)
                }
                navigateToCamps()
            }
        }
    }

    fun deleteCamp() {
        camp.value?.let {
            viewModelScope.launch {
                if (it.id.value != 0L) {
                    repository.deleteCamp(it)
                }
                navigateToCamps()
            }
        }
    }
}