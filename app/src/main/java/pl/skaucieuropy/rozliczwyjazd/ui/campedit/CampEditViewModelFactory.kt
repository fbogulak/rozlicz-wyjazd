package pl.skaucieuropy.rozliczwyjazd.ui.campedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository

class CampEditViewModelFactory(private val repository: ReckoningRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CampEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CampEditViewModel(repository) as T
        }
        throw IllegalArgumentException("Unable to construct ViewModel")
    }
}