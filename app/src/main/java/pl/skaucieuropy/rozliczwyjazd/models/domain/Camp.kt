package pl.skaucieuropy.rozliczwyjazd.models.domain

import androidx.lifecycle.MutableLiveData
import pl.skaucieuropy.rozliczwyjazd.models.database.DatabaseCamp
import java.util.*

data class Camp(
    var id: MutableLiveData<Long>,
    var name: MutableLiveData<String>,
    var budget: MutableLiveData<Double>,
    var startDate: MutableLiveData<Date>,
    var endDate: MutableLiveData<Date>,
    var isActive: MutableLiveData<Boolean>
) {
    companion object {
        fun empty(): Camp {
            return Camp(
                MutableLiveData(0),
                MutableLiveData(""),
                MutableLiveData(0.0),
                MutableLiveData(Calendar.getInstance().time),
                MutableLiveData(Calendar.getInstance().time),
                MutableLiveData(false)
            )
        }

        fun default(): Camp {
            return Camp(
                MutableLiveData(0),
                MutableLiveData("Obóz 1"),
                MutableLiveData(0.0),
                MutableLiveData(Calendar.getInstance().time),
                MutableLiveData(Calendar.getInstance().time),
                MutableLiveData(true)
            )
        }
    }
}

fun Camp.asDatabaseModel(): DatabaseCamp {
    return DatabaseCamp(
        id.value ?: 0,
        name.value ?: "",
        budget.value ?: 0.0,
        startDate.value?.time ?: Calendar.getInstance().timeInMillis,
        endDate.value?.time ?: Calendar.getInstance().timeInMillis,
        isActive.value ?: false,
    )
}