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
import java.util.*

class DocumentEditViewModel(private val repository: BaseRepository) : BaseViewModel() {

    private var _document = Document.empty()
    val document: Document
        get() = _document

    private var _documentHasLoadedFromDb = false
    val documentHasLoadedFromDb: Boolean
        get() = _documentHasLoadedFromDb

    val documentNumber = MutableLiveData(document.number.value)
    val documentType = MutableLiveData(document.type.value)
    val documentDate = MutableLiveData(document.date.value)
    val documentCategory = MutableLiveData(document.category.value)
    val documentAmount = MutableLiveData(document.amount.value)
    val documentComment = MutableLiveData(document.comment.value)
    val documentIsFromTroopAccount = MutableLiveData(document.isFromTroopAccount.value)
    val documentIsFromTravelVoucher = MutableLiveData(document.isFromTravelVoucher.value)

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
        document.id.value?.let {
            viewModelScope.launch {
                _document= repository.getDocumentById(it)
                documentNumber.value = document.number.value
                documentType.value = document.type.value
                documentDate.value = document.date.value
                documentCategory.value = document.category.value
                documentAmount.value = document.amount.value
                documentComment.value = document.comment.value
                documentIsFromTroopAccount.value = document.isFromTroopAccount.value
                documentIsFromTravelVoucher.value = document.isFromTravelVoucher.value
                _documentHasLoadedFromDb = true
                setupDatePicker()
            }
        }
    }

    fun saveDocument() {
        viewModelScope.launch {
            updateDocumentProperties()
            if (document.type.value == STATEMENT)
                document.number.value = ""
            val result = if (document.id.value == 0L) {
                document.campId.value = repository.getActiveCampId()
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
            updateDocumentProperties()
            val result = if (document.id.value != 0L) {
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

    private fun updateDocumentProperties() {
        _document.number.value = documentNumber.value ?: ""
        _document.type.value = documentType.value ?: ""
        _document.date.value = documentDate.value ?: Date()
        _document.category.value = documentCategory.value ?: ""
        _document.amount.value = documentAmount.value ?: 0.0
        _document.comment.value = documentComment.value ?: ""
        _document.isFromTroopAccount.value = documentIsFromTroopAccount.value ?: false
        _document.isFromTravelVoucher.value = documentIsFromTravelVoucher.value ?: false
    }

    fun navigateToDocuments() {
        hideKeyboard.call()
        navigationCommand.value =
            NavigationCommand.To(DocumentEditFragmentDirections.actionDocumentEditFragmentToDocumentsFragment())
    }
}