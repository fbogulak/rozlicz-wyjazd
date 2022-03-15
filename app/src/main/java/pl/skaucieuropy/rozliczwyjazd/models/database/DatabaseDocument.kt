package pl.skaucieuropy.rozliczwyjazd.models.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import pl.skaucieuropy.rozliczwyjazd.models.domain.Document
import java.util.*

@Entity(tableName = "document_table")
data class DatabaseDocument(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var number: String,
    var type: String,
    var dateMillis: Long,
    var category: String,
    var amount: Double,
    var comment: String,
    var isFromTroopAccount: Boolean,
    var isFromTravelVoucher: Boolean,
    var campId: Long
)

fun List<DatabaseDocument>.asDomainModel(): List<Document> {
    return map {
        Document(
            it.id,
            it.number,
            it.type,
            Date(it.dateMillis),
            it.category,
            it.amount,
            it.comment,
            it.isFromTroopAccount,
            it.isFromTravelVoucher,
            it.campId
        )
    }
}

fun DatabaseDocument.asDomainModel(): Document {
    return Document(
        id,
        number,
        type,
        Date(dateMillis),
        category,
        amount,
        comment,
        isFromTroopAccount,
        isFromTravelVoucher,
        campId
    )
}