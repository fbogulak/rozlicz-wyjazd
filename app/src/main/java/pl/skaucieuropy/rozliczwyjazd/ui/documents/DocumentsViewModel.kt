package pl.skaucieuropy.rozliczwyjazd.ui.documents

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DocumentsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is documents Fragment"
    }
    val text: LiveData<String> = _text
}