package pl.skaucieuropy.rozliczwyjazd.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import java.text.DecimalFormatSymbols
import java.util.*

fun String?.toDoubleOrZero(): Double {
    val localSeparator = DecimalFormatSymbols.getInstance().decimalSeparator.toString()
    return try {
        this?.replace(localSeparator, ".")?.toDouble() ?: 0.0
    } catch (e: NumberFormatException) {
        0.0
    }
}

fun String.inDoubleQuotes(): String {
    return  "\"" + this + "\""
}

fun Fragment.hideKeyboard() {
    activity?.currentFocus?.let{
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

val Calendar.today: Date
    get() {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        return time
    }