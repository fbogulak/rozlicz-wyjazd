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
    var startDate: Long,
    var endDate: Long,
    var isActive: Boolean
)

fun List<DatabaseCamp>.asDomainModel(): List<Camp> {
    return map {
        Camp(
            it.id,
            it.name,
            it.budget,
            Date(it.startDate),
            Date(it.endDate),
            it.isActive
        )
    }
}


fun DatabaseCamp.asDomainModel(): Camp {
    return Camp(
        id,
        name,
        budget,
        Date(startDate),
        Date(endDate),
        isActive
    )
}