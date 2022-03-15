package pl.skaucieuropy.rozliczwyjazd.models.database

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
    var startDateMillis: Long,
    var endDateMillis: Long,
    var isActive: Boolean
)

fun List<DatabaseCamp>.asDomainModel(): List<Camp> {
    return map {
        Camp(
            it.id,
            it.name,
            it.budget,
            Date(it.startDateMillis),
            Date(it.endDateMillis),
            it.isActive
        )
    }
}


fun DatabaseCamp.asDomainModel(): Camp {
    return Camp(
        id,
        name,
        budget,
        Date(startDateMillis),
        Date(endDateMillis),
        isActive
    )
}