package pl.skaucieuropy.rozliczwyjazd.database

import androidx.lifecycle.MutableLiveData
import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun dateFromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun mutableLiveDataFromTimestamp(value: Long?): MutableLiveData<Date> {
        return MutableLiveData(value?.let { Date(it) })
    }

    @TypeConverter
    fun mutableLivaDataToTimestamp(mutableLiveData: MutableLiveData<Date>): Long? {
        return mutableLiveData.value?.time
    }

    @TypeConverter
    fun mutableLiveDataFromString(value: String?): MutableLiveData<String> {
        return MutableLiveData(value)
    }

    @TypeConverter
    fun mutableLiveDataToString(mutableLiveData: MutableLiveData<String>): String? {
        return mutableLiveData.value
    }

    @TypeConverter
    fun mutableLiveDataFromLong(value: Long?): MutableLiveData<Long> {
        return MutableLiveData(value)
    }

    @TypeConverter
    fun mutableLiveDataToLong(mutableLiveData: MutableLiveData<Long>): Long? {
        return mutableLiveData.value
    }

    @TypeConverter
    fun mutableLiveDataFromDouble(value: Double?): MutableLiveData<Double> {
        return MutableLiveData(value)
    }

    @TypeConverter
    fun mutableLiveDataToDouble(mutableLiveData: MutableLiveData<Double>): Double? {
        return mutableLiveData.value
    }

    @TypeConverter
    fun mutableLiveDataFromBoolean(value: Boolean?): MutableLiveData<Boolean> {
        return MutableLiveData(value)
    }

    @TypeConverter
    fun mutableLiveDataToBoolean(mutableLiveData: MutableLiveData<Boolean>): Boolean? {
        return mutableLiveData.value
    }
}