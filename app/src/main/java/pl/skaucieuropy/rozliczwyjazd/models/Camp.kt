package pl.skaucieuropy.rozliczwyjazd.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "camp_table")
data class Camp(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    var name: String,
    var budget: Double,
    var startDate: Date,
    var endDate: Date,
    var isActive: Boolean
)
