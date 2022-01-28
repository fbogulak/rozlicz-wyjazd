package pl.skaucieuropy.rozliczwyjazd.ui.camps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository

class CampsViewModelFactory(private val repository: ReckoningRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CampsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CampsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unable to construct ViewModel")
    }
}