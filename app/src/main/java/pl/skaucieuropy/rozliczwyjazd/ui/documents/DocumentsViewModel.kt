package pl.skaucieuropy.rozliczwyjazd.ui.documents

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.skaucieuropy.rozliczwyjazd.models.Document
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import java.util.*

class DocumentsViewModel(private val repository: ReckoningRepository) : ViewModel() {

    val documents = repository.activeDocuments

//    private val _documents = MutableLiveData<List<Document>>().apply {
//        value = listOf(
//            Document(1, "1981/11/2019", "Faktura", Date(), "Art. spożywcze", 24.54, "", false, 1),
//            Document(2, "1783F01107/12/19", "Faktura", Date(), "Art. przemysłowe", 382.99, "", false, 1)
//        )
//    }
//    val documents: LiveData<List<Document>> = _documents

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