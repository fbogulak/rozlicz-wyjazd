package pl.skaucieuropy.rozliczwyjazd.ui.camps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository

class CampsViewModel(private val repository: ReckoningRepository) : ViewModel() {

    val camps = repository.allCamps

    private val _navigateToCampEdit = MutableLiveData<Boolean?>()
    val navigateToCampEdit: LiveData<Boolean?>
        get() = _navigateToCampEdit

    fun navigateToCampEdit() {
        _navigateToCampEdit.value = true
    }

    fun navigateToCampEditCompleted() {
        _navigateToCampEdit.value = null
    }

    fun changeActiveCamp(campId: Long?) {
        campId?.let {
            viewModelScope.launch {
                val activeCampId = repository.getActiveCampId()
                if (campId != activeCampId) {
                    repository.changeActiveCamp(it)
                }
            }
        }
    }
}