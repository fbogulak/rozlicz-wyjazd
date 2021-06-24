package pl.skaucieuropy.rozliczwyjazd.models

import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "camp_table")
data class Camp(
    @PrimaryKey(autoGenerate = true)
    var id: MutableLiveData<Long>,
    var name: MutableLiveData<String>,
    var budget: MutableLiveData<Double>,
    var startDate: MutableLiveData<Date>,
    var endDate: MutableLiveData<Date>,
    var isActive: MutableLiveData<Boolean>
) {
    companion object {
        fun empty(): Camp {
            return Camp(
                MutableLiveData(0),
                MutableLiveData(""),
                MutableLiveData(0.0),
                MutableLiveData(Calendar.getInstance().time),
                MutableLiveData(Calendar.getInstance().time),
                MutableLiveData(false)
            )
        }
    }
}