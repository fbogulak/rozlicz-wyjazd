package pl.skaucieuropy.rozliczwyjazd.ui.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit
import pl.skaucieuropy.rozliczwyjazd.models.domain.Camp
import pl.skaucieuropy.rozliczwyjazd.repository.BaseRepository
import pl.skaucieuropy.rozliczwyjazd.ui.base.BaseViewModel

class SummaryViewModel(private val repository: BaseRepository) : BaseViewModel() {

    private val _activeCamp = MutableLiveData<Camp>()
    val activeCamp: LiveData<Camp>
        get() = _activeCamp

    private val _remainingMoney = MutableLiveData<Double>()
    val remainingMoney: LiveData<Double>
        get() = _remainingMoney

    private val _remainingDays = MutableLiveData<Long>()
    val remainingDays: LiveData<Long>
        get() = _remainingDays

    private val _moneyPerDay = MutableLiveData<Double>()
    val moneyPerDay: LiveData<Double>
        get() = _moneyPerDay

    private val _groceriesPerDay = MutableLiveData<Double>()
    val groceriesPerDay: LiveData<Double>
        get() = _groceriesPerDay

    fun calculateSummary() {
        viewModelScope.launch {
            var activeCamp: Camp?
            do {
                activeCamp = repository.getActiveCamp()
                if (activeCamp == null) delay(100)
            } while (activeCamp == null)
            _activeCamp.value = activeCamp

            val activeCampExpenses = repository.getActiveCampExpenses()
            val remainingMoney = activeCamp.budget - activeCampExpenses
            _remainingMoney.value = remainingMoney

            val startDateMillis = activeCamp.startDate.time
            val endDateMillis = activeCamp.endDate.time
            val startDate =
                Instant.ofEpochMilli(startDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            val endDate =
                Instant.ofEpochMilli(endDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            val today = LocalDate.now()
            val remainingDays = when {
                today.isBefore(startDate) -> ChronoUnit.DAYS.between(startDate, endDate) + 1
                today.isBefore(endDate) -> ChronoUnit.DAYS.between(today, endDate) + 1
                today.isEqual(endDate) -> 1
                else -> 0
            }
            _remainingDays.value = remainingDays

            _moneyPerDay.value = if (remainingMoney <= 0.0 || remainingDays == 0L) {
                0.0
            } else {
                remainingMoney / remainingDays
            }

            val daysFromStartDate = when {
                today.isAfter(endDate) -> ChronoUnit.DAYS.between(startDate, endDate) + 1
                today.isAfter(startDate) -> ChronoUnit.DAYS.between(startDate, today) + 1
                today.isEqual(startDate) -> 1
                else -> 0
            }
            val groceriesExpenses = repository.getActiveCampGroceriesExpenses()
            _groceriesPerDay.value = if (daysFromStartDate == 0L) {
                0.0
            } else {
                groceriesExpenses / daysFromStartDate
            }
        }
    }
}