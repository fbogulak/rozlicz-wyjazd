package pl.skaucieuropy.rozliczwyjazd.ui.camps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.constants.STATEMENT
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import pl.skaucieuropy.rozliczwyjazd.models.FileData
import pl.skaucieuropy.rozliczwyjazd.repository.BaseRepository
import pl.skaucieuropy.rozliczwyjazd.ui.base.BaseViewModel
import pl.skaucieuropy.rozliczwyjazd.ui.base.NavigationCommand
import pl.skaucieuropy.rozliczwyjazd.utils.inDoubleQuotes
import pl.skaucieuropy.rozliczwyjazd.utils.inDoubleQuotesOrEmpty
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class CampsViewModel(private val repository: BaseRepository) : BaseViewModel() {

    val camps = repository.allCamps

    private val _createExportFile = MutableLiveData<FileData?>()
    val createExportFile: LiveData<FileData?>
        get() = _createExportFile

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
                var type: String
                var date: String

                for (document in documents) {
                    date = document.date.value?.let { dateFormat.format(it) } ?: ""
                    documentData.add(date)

                    type = document.type.value ?: ""
                    documentData.add(
                        if (type == STATEMENT)
                            "$type z dnia $date"
                        else
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

    fun navigateToCampEdit(campId: Long, destinationLabel: String) {
        navigationCommand.value = NavigationCommand.To(
            CampsFragmentDirections.actionCampsFragmentToCampEditFragment(
                campId, destinationLabel
            )
        )
    }
}