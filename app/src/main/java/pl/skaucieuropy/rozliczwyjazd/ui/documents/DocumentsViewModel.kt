package pl.skaucieuropy.rozliczwyjazd.ui.documents

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.skaucieuropy.rozliczwyjazd.models.domain.Document
import pl.skaucieuropy.rozliczwyjazd.repository.BaseRepository
import pl.skaucieuropy.rozliczwyjazd.ui.base.BaseViewModel
import pl.skaucieuropy.rozliczwyjazd.ui.base.NavigationCommand
import pl.skaucieuropy.rozliczwyjazd.utils.DocumentsOrderBy

class DocumentsViewModel(private val repository: BaseRepository) : BaseViewModel() {

    private val _documents = MutableLiveData<MutableList<Document>>()
    val documents: LiveData<MutableList<Document>>
        get() = _documents

    var isSearching = false

    fun getDocuments(query: String?, orderBy: DocumentsOrderBy) {
        viewModelScope.launch {
            _documents.value =
                repository.getActiveDocuments(query, orderBy) as MutableList<Document>
        }
    }

    fun navigateToDocumentEdit(documentId: Long, destinationLabel: String) {
        navigationCommand.value = NavigationCommand.To(
            DocumentsFragmentDirections.actionDocumentsFragmentToDocumentEditFragment(
                documentId, destinationLabel
            )
        )
    }
}