package pl.skaucieuropy.rozliczwyjazd.ui.documentedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.skaucieuropy.rozliczwyjazd.models.Document
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository

class DocumentEditViewModel(private val repository: ReckoningRepository) : ViewModel() {

    val document = MutableLiveData(Document.empty())

    private val _navigateToDocuments = MutableLiveData<Boolean?>()
    val navigateToDocuments: LiveData<Boolean?>
        get() = _navigateToDocuments

    fun navigateToDocuments() {
        _navigateToDocuments.value = true
    }

    fun navigateToDocumentsCompleted() {
        _navigateToDocuments.value = null
    }

    fun getDocumentFromDb() {
        document.value?.id?.value?.let {
            viewModelScope.launch {
                document.value = repository.getDocumentById(it)
            }
        }
    }

    fun saveDocument() {
        document.value?.let {
            viewModelScope.launch {
                if (it.id.value == 0L) {
                    it.campId.value = repository.getActiveCampId()
                    repository.insertDocument(it)
                } else {
                    repository.updateDocument(it)
                }
            }
            navigateToDocuments()
        }
    }

    fun deleteDocument() {
        document.value?.let {
            viewModelScope.launch {
                if (it.id.value != 0L) {
                    repository.deleteDocument(it)
                }
            }
            navigateToDocuments()
        }
    }
}