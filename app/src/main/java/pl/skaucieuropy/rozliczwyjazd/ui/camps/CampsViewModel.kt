package pl.skaucieuropy.rozliczwyjazd.ui.camps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CampsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is camps Fragment"
    }
    val text: LiveData<String> = _text
}