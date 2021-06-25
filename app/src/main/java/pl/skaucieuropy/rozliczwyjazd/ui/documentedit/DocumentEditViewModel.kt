package pl.skaucieuropy.rozliczwyjazd.ui.documentedit

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import pl.skaucieuropy.rozliczwyjazd.models.Document
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository

class DocumentEditViewModel(private val repository: ReckoningRepository) : ViewModel() {

    val document = MutableLiveData(Document.empty())
    var originalDocument = Document.empty()
    var documentHasChanged = false
    var documentHasLoadedFromDb = false

    private val _setupDatePicker = MutableLiveData<Boolean?>()
    val setupDatePicker: LiveData<Boolean?>
        get() = _setupDatePicker

    private val _navigateToDocuments = MutableLiveData<Boolean?>()
    val navigateToDocuments: LiveData<Boolean?>
        get() = _navigateToDocuments

    fun setupDatePicker() {
        _setupDatePicker.value = true
    }

    fun setupDatePickerCompleted() {
        _setupDatePicker.value = null
    }

    fun navigateToDocuments() {
        _navigateToDocuments.value = true
    }

    fun navigateToDocumentsCompleted() {
        _navigateToDocuments.value = null
    }

    fun getDocumentFromDb() {
        document.value?.id?.value?.let {
            viewModelScope.launch {
                originalDocument = repository.getDocumentById(it)
                document.value = originalDocument
                documentHasLoadedFromDb = true
                setupDatePicker()
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
                navigateToDocuments()
            }
        }
    }

    fun deleteDocument() {
        document.value?.let {
            viewModelScope.launch {
                if (it.id.value != 0L) {
                    repository.deleteDocument(it)
                }
                navigateToDocuments()
            }
        }
    }
}