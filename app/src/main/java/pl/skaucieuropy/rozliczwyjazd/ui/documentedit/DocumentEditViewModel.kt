package pl.skaucieuropy.rozliczwyjazd.ui.documentedit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.skaucieuropy.rozliczwyjazd.models.Document
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import java.util.*

class DocumentEditViewModel(private val repository: ReckoningRepository) : ViewModel() {

    val document = MutableLiveData(Document.empty())

    fun getDocumentFromDb(documentId: Long) {
        viewModelScope.launch {
            document.value = repository.getDocumentById(documentId)
        }
    }
}