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

    val documentNumber = MutableLiveData(document.number)
    val documentType = MutableLiveData(document.type)
    val documentDate = MutableLiveData(document.date)
    val documentCategory = MutableLiveData(document.category)
    val documentAmount = MutableLiveData(document.amount)
    val documentComment = MutableLiveData(document.comment)
    val documentIsFromTroopAccount = MutableLiveData(document.isFromTroopAccount)
    val documentIsFromTravelVoucher = MutableLiveData(document.isFromTravelVoucher)

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
            _document = repository.getDocumentById(document.id)
            documentNumber.value = document.number
            documentType.value = document.type
            documentDate.value = document.date
            documentCategory.value = document.category
            documentAmount.value = document.amount
            documentComment.value = document.comment
            documentIsFromTroopAccount.value = document.isFromTroopAccount
            documentIsFromTravelVoucher.value = document.isFromTravelVoucher
            _documentHasLoadedFromDb = true
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
            updateDocumentProperties()
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

    fun updateDocumentProperties() {
        _document.number = documentNumber.value ?: ""
        _document.type = documentType.value ?: ""
        _document.date = documentDate.value ?: Date()
        _document.category = documentCategory.value ?: ""
        _document.amount = documentAmount.value ?: 0.0
        _document.comment = documentComment.value ?: ""
        _document.isFromTroopAccount = documentIsFromTroopAccount.value ?: false
        _document.isFromTravelVoucher = documentIsFromTravelVoucher.value ?: false
    }

    fun navigateToDocuments() {
        hideKeyboard.call()
        navigationCommand.value =
            NavigationCommand.To(DocumentEditFragmentDirections.actionDocumentEditFragmentToDocumentsFragment())
    }
}