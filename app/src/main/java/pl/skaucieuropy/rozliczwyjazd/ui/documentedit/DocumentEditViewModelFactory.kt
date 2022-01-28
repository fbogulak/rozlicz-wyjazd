package pl.skaucieuropy.rozliczwyjazd.ui.documentedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import pl.skaucieuropy.rozliczwyjazd.ui.documents.DocumentsViewModel

class DocumentEditViewModelFactory(private val repository: ReckoningRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DocumentEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DocumentEditViewModel(repository) as T
        }
        throw IllegalArgumentException("Unable to construct ViewModel")
    }
}