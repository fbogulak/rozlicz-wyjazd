package pl.skaucieuropy.rozliczwyjazd.ui.summary

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository

class SummaryViewModel(private val repository: ReckoningRepository) : ViewModel() {

    val camp = repository.activeCamp
    val campExpenses = Transformations.map(repository.activeCampExpenses) {
        it ?: 0.0
    }
}