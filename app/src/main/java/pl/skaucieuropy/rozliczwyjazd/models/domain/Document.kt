package pl.skaucieuropy.rozliczwyjazd.models.domain

import androidx.lifecycle.MutableLiveData
import pl.skaucieuropy.rozliczwyjazd.models.database.DatabaseDocument
import java.util.*

data class Document(
    var id: MutableLiveData<Long>,
    var number: MutableLiveData<String>,
    var type: MutableLiveData<String>,
    var date: MutableLiveData<Date>,
    var category: MutableLiveData<String>,
    var amount: MutableLiveData<Double>,
    var comment: MutableLiveData<String>,
    var isFromTroopAccount: MutableLiveData<Boolean>,
    var isFromTravelVoucher: MutableLiveData<Boolean>,
    var campId: MutableLiveData<Long>
) {
    companion object {
        fun empty(): Document {
            return Document(
                MutableLiveData(0),
                MutableLiveData(""),
                MutableLiveData(""),
                MutableLiveData(Calendar.getInstance().time),
                MutableLiveData(""),
                MutableLiveData(0.0),
                MutableLiveData(""),
                MutableLiveData(false),
                MutableLiveData(false),
                MutableLiveData(0)
            )
        }
    }
}

fun Document.asDatabaseModel(): DatabaseDocument {
    return DatabaseDocument(
        id.value ?: 0,
        number.value ?: "",
        type.value ?: "",
        date.value?.time ?: Calendar.getInstance().timeInMillis,
        category.value ?: "",
        amount.value ?: 0.0,
        comment.value ?: "",
        isFromTroopAccount.value ?: false,
        isFromTravelVoucher.value ?: false,
        campId.value ?: 0,
    )
}