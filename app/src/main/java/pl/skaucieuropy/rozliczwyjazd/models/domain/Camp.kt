package pl.skaucieuropy.rozliczwyjazd.models.domain

import pl.skaucieuropy.rozliczwyjazd.models.database.DatabaseCamp
import java.util.*

data class Camp(
    var id: Long,
    var name: String,
    var budget: Double,
    var startDate: Date,
    var endDate: Date,
    var isActive: Boolean
) {
    companion object {
        fun empty(): Camp {
            return Camp(
                0,
                "",
                0.0,
                Calendar.getInstance().time,
                Calendar.getInstance().time,
                false
            )
        }

        fun default(): Camp {
            return Camp(
                0,
                "Obóz 1",
                0.0,
                Calendar.getInstance().time,
                Calendar.getInstance().time,
                true
            )
        }
    }
}

fun Camp.asDatabaseModel(): DatabaseCamp {
    return DatabaseCamp(
        id,
        name,
        budget,
        startDate.time,
        endDate.time,
        isActive,
    )
}