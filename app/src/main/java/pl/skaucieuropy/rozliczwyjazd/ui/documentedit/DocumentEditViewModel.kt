package pl.skaucieuropy.rozliczwyjazd.ui.documentedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.constants.STATEMENT
import pl.skaucieuropy.rozliczwyjazd.models.Document
import pl.skaucieuropy.rozliczwyjazd.repository.BaseRepository
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import pl.skaucieuropy.rozliczwyjazd.ui.base.BaseViewModel
import pl.skaucieuropy.rozliczwyjazd.ui.base.NavigationCommand

class DocumentEditViewModel(private val repository: BaseRepository) : BaseViewModel() {

    val document = MutableLiveData(Document.empty())
    var originalDocument = Document.empty()
    var documentHasChanged = false
    var documentHasLoadedFromDb = false

    private val _setupDatePicker = MutableLiveData<Boolean?>()
    val setupDatePicker: LiveData<Boolean?>
        get() = _setupDatePicker

    fun setupDatePicker() {
        _setupDatePicker.value = true
    }

    fun setupDatePickerCompleted() {
        _setupDatePicker.value = null
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
        val currentDocument = document.value
        if (currentDocument != null) {
            viewModelScope.launch {
                if (currentDocument.type.value == STATEMENT)
                    currentDocument.number.value = ""
                val result = if (currentDocument.id.value == 0L) {
                    currentDocument.campId.value = repository.getActiveCampId()
                    repository.insertDocument(currentDocument)
                } else {
                    repository.updateDocument(currentDocument)
                }
                result.onSuccess {
                    showToast(it)
                }
                result.onFailure {
                    val message = it.message
                    if (message != null) {
                        showToast(message)
                    } else {
                        showToast(R.string.error_saving_document)
                    }
                }
                navigateToDocuments()
            }
        } else {
            showToast(R.string.error_saving_document)
        }
    }

    fun deleteDocument() {
        val currentDocument = document.value
        if (currentDocument != null) {
            viewModelScope.launch {
                val result = if (currentDocument.id.value != 0L) {
                    repository.deleteDocument(currentDocument)
                } else {
                    Result.failure(Throwable(ReckoningRepository.ERROR_DELETING_DOCUMENT))
                }
                result.onSuccess {
                    showToast(it)
                }
                result.onFailure {
                    val message = it.message
                    if (message != null) {
                        showToast(message)
                    } else {
                        showToast(R.string.error_deleting_document)
                    }
                }
                navigateToDocuments()
            }
        } else {
            showToast(R.string.error_deleting_document)
        }
    }

    fun navigateToDocuments() {
        hideKeyboard.call()
        navigationCommand.value =
            NavigationCommand.To(DocumentEditFragmentDirections.actionDocumentEditFragmentToDocumentsFragment())
    }
}