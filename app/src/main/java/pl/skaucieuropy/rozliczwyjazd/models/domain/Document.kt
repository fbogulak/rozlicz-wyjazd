package pl.skaucieuropy.rozliczwyjazd.models.domain

import pl.skaucieuropy.rozliczwyjazd.models.database.DatabaseDocument
import java.util.*

data class Document(
    var id: Long,
    var number: String,
    var type: String,
    var date: Date,
    var creationTimestamp: Date,
    var category: String,
    var amount: Double,
    var comment: String,
    var isFromTroopAccount: Boolean,
    var isFromTravelVoucher: Boolean,
    var campId: Long
) {
    companion object {
        fun empty(): Document {
            return Document(
                0,
                "",
                "",
                Calendar.getInstance().time,
                Calendar.getInstance().time,
                "",
                0.0,
                "",
                isFromTroopAccount = false,
                isFromTravelVoucher = false,
                0
            )
        }
    }
}

fun Document.asDatabaseModel(): DatabaseDocument {
    return DatabaseDocument(
        id,
        number,
        type,
        date.time,
        creationTimestamp.time,
        category,
        amount,
        comment,
        isFromTroopAccount,
        isFromTravelVoucher,
        campId
    )
}