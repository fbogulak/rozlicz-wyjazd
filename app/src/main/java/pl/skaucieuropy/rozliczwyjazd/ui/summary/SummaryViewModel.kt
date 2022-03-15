package pl.skaucieuropy.rozliczwyjazd.ui.summary

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit
import pl.skaucieuropy.rozliczwyjazd.models.domain.Camp
import pl.skaucieuropy.rozliczwyjazd.repository.BaseRepository
import pl.skaucieuropy.rozliczwyjazd.ui.base.BaseViewModel

class SummaryViewModel(private val repository: BaseRepository) : BaseViewModel() {

    private var _camp: Camp? = null
    val camp: Camp?
        get() = _camp

    private var _remainingMoney = 0.0
    val remainingMoney: Double
        get() = _remainingMoney

    private var _remainingDaysString = ""
    val remainingDaysString: String
        get() = _remainingDaysString

    private var _moneyPerDay = 0.0
    val moneyPerDay: Double
        get() = _moneyPerDay

    init {
        viewModelScope.launch {
            val camp = repository.getActiveCamp()
            _camp = camp

            val campExpenses = repository.getActiveCampExpenses()
            val remainingMoney = camp.budget - campExpenses
            _remainingMoney = remainingMoney

            val startDateMillis = camp.startDate.time
            val endDateMillis = camp.endDate.time
            val startDate =
                Instant.ofEpochMilli(startDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            val endDate =
                Instant.ofEpochMilli(endDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            val today = LocalDate.now()
            val remainingDays =
                when {
                    today.isBefore(startDate) -> ChronoUnit.DAYS.between(startDate, endDate) + 1
                    today.isBefore(endDate) -> ChronoUnit.DAYS.between(today, endDate) + 1
                    today.isEqual(endDate) -> 1
                    else -> 0
                }
            _remainingDaysString = remainingDays.toString()

            _moneyPerDay = if (remainingMoney <= 0.0 || remainingDays == 0L) {
                0.0
            } else {
                remainingMoney / remainingDays
            }
        }
    }
}