package pl.skaucieuropy.rozliczwyjazd.ui.documentedit.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("date")
fun bindTextViewToDate(textView: TextView, date: Date?) {
    date?.let {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        textView.text = dateFormat.format(it)
    }
}