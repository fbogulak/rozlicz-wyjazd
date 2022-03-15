package pl.skaucieuropy.rozliczwyjazd.ui.camps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.constants.STATEMENT
import pl.skaucieuropy.rozliczwyjazd.models.domain.Camp
import pl.skaucieuropy.rozliczwyjazd.models.domain.FileData
import pl.skaucieuropy.rozliczwyjazd.repository.BaseRepository
import pl.skaucieuropy.rozliczwyjazd.ui.base.BaseViewModel
import pl.skaucieuropy.rozliczwyjazd.ui.base.NavigationCommand
import pl.skaucieuropy.rozliczwyjazd.utils.inDoubleQuotes
import pl.skaucieuropy.rozliczwyjazd.utils.inDoubleQuotesOrEmpty
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class CampsViewModel(private val repository: BaseRepository) : BaseViewModel() {

    private var _camps: LiveData<List<Camp>>? = null
    val camps: LiveData<List<Camp>>?
        get() = _camps

    init {
        viewModelScope.launch {
            _camps = repository.getAllCamps()
        }
    }

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
        camp?.id?.let {
            viewModelScope.launch {
                val documents = repository.getDocumentsByCampId(it)
                val stringBuilder = StringBuilder().append("SEP=,\n")
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val decimalFormat = DecimalFormat("0.00")

                val documentData = mutableListOf<String>()
                var date: String

                for (document in documents) {
                    date = dateFormat.format(document.date)
                    documentData.add(date)

                    documentData.add(
                        if (document.type == STATEMENT)
                            "$document.type z dnia $date"
                        else
                            (document.type + " nr " + document.number).inDoubleQuotesOrEmpty()
                    )

                    documentData.add(document.category.inDoubleQuotesOrEmpty())
                    documentData.add(if (document.isFromTroopAccount) "TAK" else "NIE")
                    documentData.add(decimalFormat.format(document.amount).inDoubleQuotes())
                    documentData.add(if (document.isFromTravelVoucher) "TAK" else "NIE")
                    documentData.add(document.comment.inDoubleQuotesOrEmpty())

                    for (value in documentData) {
                        stringBuilder.append(value).append(",")
                    }
                    stringBuilder.append("\n")

                    documentData.clear()
                }
                val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH_mm_ss", Locale.getDefault())
                val fileName =
                    camp.name + " " + dateTimeFormat.format(Date()) + ".csv"
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