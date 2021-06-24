package pl.skaucieuropy.rozliczwyjazd.ui.camps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
}