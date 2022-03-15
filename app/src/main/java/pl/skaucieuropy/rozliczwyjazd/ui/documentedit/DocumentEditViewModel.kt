package pl.skaucieuropy.rozliczwyjazd.ui.documentedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.constants.STATEMENT
import pl.skaucieuropy.rozliczwyjazd.models.domain.Document
import pl.skaucieuropy.rozliczwyjazd.repository.BaseRepository
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import pl.skaucieuropy.rozliczwyjazd.ui.base.BaseViewModel
import pl.skaucieuropy.rozliczwyjazd.ui.base.NavigationCommand

class DocumentEditViewModel(private val repository: BaseRepository) : BaseViewModel() {

    var document = Document.empty()
    var originalDocument = Document.empty()
    var documentHasChanged = document != originalDocument //TODO Check if it works correctly
    var documentHasLoadedFromDb = false

    val isFromTravelVoucher = MutableLiveData<Boolean>()
    val isFromTroopAccount = MutableLiveData<Boolean>()

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
        viewModelScope.launch {
            originalDocument = repository.getDocumentById(document.id)
            document = originalDocument
            documentHasLoadedFromDb = true
            setupDatePicker()

        }
    }

    fun saveDocument() {
        viewModelScope.launch {
            if (document.type == STATEMENT)
                document.number = ""
            val result = if (document.id == 0L) {
                document.campId = repository.getActiveCampId()
                repository.insertDocument(document)
            } else {
                repository.updateDocument(document)
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
    }

    fun deleteDocument() {
        viewModelScope.launch {
            val result = if (document.id != 0L) {
                repository.deleteDocument(document)
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
    }

    fun navigateToDocuments() {
        hideKeyboard.call()
        navigationCommand.value =
            NavigationCommand.To(DocumentEditFragmentDirections.actionDocumentEditFragmentToDocumentsFragment())
    }
}