package pl.skaucieuropy.rozliczwyjazd.ui.documents

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import pl.skaucieuropy.rozliczwyjazd.models.domain.Document
import pl.skaucieuropy.rozliczwyjazd.repository.BaseRepository
import pl.skaucieuropy.rozliczwyjazd.ui.base.BaseViewModel
import pl.skaucieuropy.rozliczwyjazd.ui.base.NavigationCommand

class DocumentsViewModel(private val repository: BaseRepository) : BaseViewModel() {

    val searchQuery = MutableLiveData("")
    val documents = searchQuery.switchMap<String, List<Document>> {
        repository.getActiveDocuments(it)
    }
    var isSearching = false

    fun navigateToDocumentEdit(documentId: Long, destinationLabel: String) {
        navigationCommand.value = NavigationCommand.To(
            DocumentsFragmentDirections.actionDocumentsFragmentToDocumentEditFragment(
                documentId, destinationLabel
            )
        )
    }
}