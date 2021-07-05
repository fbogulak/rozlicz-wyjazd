package pl.skaucieuropy.rozliczwyjazd.ui.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository

class SummaryViewModelFactory(private val repository: ReckoningRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SummaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SummaryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unable to construct ViewModel")
    }
}