package pl.skaucieuropy.rozliczwyjazd.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import java.text.DecimalFormatSymbols

fun String?.toDoubleOrZero(): Double {
    val localSeparator = DecimalFormatSymbols.getInstance().decimalSeparator.toString()
    return try {
        this?.replace(localSeparator, ".")?.toDouble() ?: 0.0
    } catch (e: NumberFormatException) {
        0.0
    }
}

fun <T, K, R> LiveData<T>.combineWith(
    liveData: LiveData<K>,
    block: (T?, K?) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) {
        result.value = block(this.value, liveData.value)
    }
    result.addSource(liveData) {
        result.value = block(this.value, liveData.value)
    }
    return result
}

fun String.inDoubleQuotes(): String {
    return  "\"" + this + "\""
}

fun String?.inDoubleQuotesOrEmpty(): String {
    return this?.let { "\"" + it + "\"" } ?: ""
}

fun Fragment.hideKeyboard() {
    activity?.currentFocus?.let{
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
    }
}