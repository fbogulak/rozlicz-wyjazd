package pl.skaucieuropy.rozliczwyjazd.models.database

import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.PrimaryKey
import pl.skaucieuropy.rozliczwyjazd.models.domain.Camp
import java.util.*

@Entity(tableName = "camp_table")
data class DatabaseCamp(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var name: String,
    var budget: Double,
    var startDate: Long,
    var endDate: Long,
    var isActive: Boolean
)

fun List<DatabaseCamp>.asDomainModel(): List<Camp> {
    return map {
        Camp(
            MutableLiveData(it.id),
            MutableLiveData(it.name),
            MutableLiveData(it.budget),
            MutableLiveData(Date(it.startDate)),
            MutableLiveData(Date(it.endDate)),
            MutableLiveData(it.isActive)
        )
    }
}


fun DatabaseCamp.asDomainModel(): Camp {
    return Camp(
        MutableLiveData(id),
        MutableLiveData(name),
        MutableLiveData(budget),
        MutableLiveData(Date(startDate)),
        MutableLiveData(Date(endDate)),
        MutableLiveData(isActive)
    )
}