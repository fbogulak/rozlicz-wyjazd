package pl.skaucieuropy.rozliczwyjazd.ui.documentedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class DocumentEditViewModel : ViewModel() {

    private val _selectedDate = MutableLiveData(Calendar.getInstance().time)

    val selectedDate: LiveData<Date>
        get() = _selectedDate

    fun updateSelectedDate(newDate: Date) {
        _selectedDate.value = newDate
    }
}