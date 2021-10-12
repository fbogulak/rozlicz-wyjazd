package pl.skaucieuropy.rozliczwyjazd.ui.documents

import androidx.lifecycle.*
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository

class DocumentsViewModel(private val repository: ReckoningRepository) : ViewModel() {

    val searchQuery = MutableLiveData("")
    val documents = searchQuery.switchMap {
       repository.getActiveDocuments(it)
    }

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