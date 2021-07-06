package pl.skaucieuropy.rozliczwyjazd.ui.summary.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.NumberFormat
import java.util.*

@BindingAdapter("amount")
fun bindTextViewToAmount(textView: TextView, amount: Double?) {
    amount?.let {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.currency = Currency.getInstance("PLN")

        textView.text = format.format(amount)
    }
}