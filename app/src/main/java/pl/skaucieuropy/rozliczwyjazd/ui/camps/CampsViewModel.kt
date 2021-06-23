package pl.skaucieuropy.rozliczwyjazd.ui.camps

import androidx.lifecycle.ViewModel
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository

class CampsViewModel(private val repository: ReckoningRepository) : ViewModel() {

    val camps = repository.allCamps
}