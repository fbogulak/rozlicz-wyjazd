package pl.skaucieuropy.rozliczwyjazd.models.database

import androidx.lifecycle.MutableLiveData
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
    var date: Long,
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
            MutableLiveData(it.id),
            MutableLiveData(it.number),
            MutableLiveData(it.type),
            MutableLiveData(Date(it.date)),
            MutableLiveData(it.category),
            MutableLiveData(it.amount),
            MutableLiveData(it.comment),
            MutableLiveData(it.isFromTroopAccount),
            MutableLiveData(it.isFromTravelVoucher),
            MutableLiveData(it.campId)
        )
    }
}

fun DatabaseDocument.asDomainModel(): Document {
    return Document(
        MutableLiveData(id),
        MutableLiveData(number),
        MutableLiveData(type),
        MutableLiveData(Date(date)),
        MutableLiveData(category),
        MutableLiveData(amount),
        MutableLiveData(comment),
        MutableLiveData(isFromTroopAccount),
        MutableLiveData(isFromTravelVoucher),
        MutableLiveData(campId)
    )
}