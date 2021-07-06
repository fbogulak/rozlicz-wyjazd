package pl.skaucieuropy.rozliczwyjazd.ui.summary

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import pl.skaucieuropy.rozliczwyjazd.utils.combineWith

class SummaryViewModel(private val repository: ReckoningRepository) : ViewModel() {

    val camp = repository.activeCamp

    val remainingMoney = camp.combineWith(repository.activeCampExpenses) { camp, campExpenses ->
        val budget = camp?.budget?.value ?: 0.0
        val expenses = campExpenses ?: 0.0
        budget - expenses
    }

    private val remainingDays = Transformations.map(camp) {
        val startDateMillis = it.startDate.value?.time
        val endDateMillis = it.endDate.value?.time

        if (startDateMillis != null && endDateMillis != null) {
            val startDate =
                Instant.ofEpochMilli(startDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            val endDate =
                Instant.ofEpochMilli(endDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            val today = LocalDate.now()
            when {
                today.isBefore(startDate) -> ChronoUnit.DAYS.between(startDate, endDate) + 1
                today.isBefore(endDate) -> ChronoUnit.DAYS.between(today, endDate) + 1
                today.isEqual(endDate) -> 1
                else -> 0
            }
        } else 0
    }

    val remainingDaysString = Transformations.map(remainingDays) {
        it.toString()
    }

    val moneyPerDay = remainingMoney.combineWith(remainingDays) { money, days ->
        if (days == null || days == 0L) {
            0.0
        } else {
            (money ?: 0.0) / days
        }
    }
}