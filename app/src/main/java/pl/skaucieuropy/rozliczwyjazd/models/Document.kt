package pl.skaucieuropy.rozliczwyjazd.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "document_table")
data class Document(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    var number: String,
    var type: String,
    var date: Date,
    var category: String,
    var amount: Double,
    var comment: String,
    var isFromTroopAccount: Boolean,
    var campId: Long
)