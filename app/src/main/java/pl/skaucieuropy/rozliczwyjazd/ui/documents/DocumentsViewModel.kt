package pl.skaucieuropy.rozliczwyjazd.ui.documents

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository

class DocumentsViewModel(private val repository: ReckoningRepository) : ViewModel() {

    val documents = repository.activeDocuments

    private val _navigateToDocumentEdit = MutableLiveData<Boolean?>()
    val navigateToDocumentEdit: LiveData<Boolean?>
        get() = _navigateToDocumentEdit

    fun navigateToDocumentEdit() {
        _navigateToDocumentEdit.value = true
    }

    fun navigateToDocumentEditCompleted() {
        _navigateToDocumentEdit.value = null
    }
}