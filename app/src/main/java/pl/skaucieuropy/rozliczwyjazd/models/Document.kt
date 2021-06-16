package pl.skaucieuropy.rozliczwyjazd.models

import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "document_table")
data class Document(
    @PrimaryKey(autoGenerate = true)
    var id: MutableLiveData<Long>,
    var number: MutableLiveData<String>,
    var type: MutableLiveData<String>,
    var date: MutableLiveData<Date>,
    var category: MutableLiveData<String>,
    var amount: MutableLiveData<Double>,
    var comment: MutableLiveData<String>,
    var isFromTroopAccount: MutableLiveData<Boolean>,
    var campId: MutableLiveData<Long>
)