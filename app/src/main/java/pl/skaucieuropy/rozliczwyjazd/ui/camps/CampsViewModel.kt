package pl.skaucieuropy.rozliczwyjazd.ui.camps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import pl.skaucieuropy.rozliczwyjazd.models.FileData
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import pl.skaucieuropy.rozliczwyjazd.utils.ToastMessage
import pl.skaucieuropy.rozliczwyjazd.utils.inDoubleQuotes
import pl.skaucieuropy.rozliczwyjazd.utils.inDoubleQuotesOrEmpty
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class CampsViewModel(private val repository: ReckoningRepository) : ViewModel() {

    val camps = repository.allCamps

    private val _navigateToCampEdit = MutableLiveData<Boolean?>()
    val navigateToCampEdit: LiveData<Boolean?>
        get() = _navigateToCampEdit

    private val _showToast = MutableLiveData<ToastMessage<*>?>()
    val showToast: LiveData<ToastMessage<*>?>
        get() = _showToast

    private val _createExportFile = MutableLiveData<FileData?>()
    val createExportFile: LiveData<FileData?>
        get() = _createExportFile

    fun navigateToCampEdit() {
        _navigateToCampEdit.value = true
    }

    fun navigateToCampEditCompleted() {
        _navigateToCampEdit.value = null
    }

    private fun showToast(message: String) {
        _showToast.value = ToastMessage.from(message)
    }

    private fun showToast(messageResId: Int) {
        _showToast.value = ToastMessage.from(messageResId)
    }

    fun showToastCompleted() {
        _showToast.value = null
    }

    private fun createExportFile(fileData: FileData?) {
        _createExportFile.value = fileData
    }

    fun createExportFileCompleted() {
        _createExportFile.value = null
    }

    fun changeActiveCamp(campId: Long?) {
        if (campId != null) {
            viewModelScope.launch {
                val activeCampId = repository.getActiveCampId()
                val result = if (campId == activeCampId) {
                    Result.success(R.string.camp_already_active)
                } else {
                    repository.changeActiveCamp(campId)
                }
                result.onSuccess {
                    showToast(it)
                }
                result.onFailure {
                    val message = it.message
                    if (message != null) {
                        showToast(message)
                    } else {
                        showToast(R.string.error_setting_as_active)
                    }
                }
            }
        } else {
            showToast(R.string.error_setting_as_active)
        }
    }

    fun exportToCsv(camp: Camp?) {
        camp?.id?.value?.let {
            viewModelScope.launch {
                val documents = repository.getDocumentsByCampId(it)
                val stringBuilder = StringBuilder().append("SEP=,\n")
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val decimalFormat = DecimalFormat("0.00")

                val documentData = mutableListOf<String>()

                for (document in documents) {
                    documentData.add(document.date.value?.let { dateFormat.format(it) } ?: "")
                    documentData.add(
                        (document.type.value + " nr " + document.number.value).inDoubleQuotesOrEmpty()
                    )
                    documentData.add(document.category.value.inDoubleQuotesOrEmpty())
                    documentData.add(document.isFromTroopAccount.value?.let { if (it) "TAK" else "NIE" }
                        ?: "")
                    documentData.add(document.amount.value?.let {
                        decimalFormat.format(it).inDoubleQuotes()
                    } ?: "")
                    documentData.add(document.isFromTravelVoucher.value?.let { if (it) "TAK" else "NIE" }
                        ?: "")
                    documentData.add(document.comment.value.inDoubleQuotesOrEmpty())

                    for (value in documentData) {
                        stringBuilder.append(value).append(",")
                    }
                    stringBuilder.append("\n")

                    documentData.clear()
                }
                val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH_mm_ss", Locale.getDefault())
                val fileName =
                    (camp.name.value ?: "") + " " + dateTimeFormat.format(Date()) + ".csv"
                createExportFile(FileData(fileName, stringBuilder.toString()))
            }
        }
    }
}