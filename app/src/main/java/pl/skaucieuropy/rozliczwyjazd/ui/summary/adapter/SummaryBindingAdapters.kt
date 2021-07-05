package pl.skaucieuropy.rozliczwyjazd.ui.summary.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import pl.skaucieuropy.rozliczwyjazd.models.Document
import pl.skaucieuropy.rozliczwyjazd.ui.documents.adapter.DocumentsListAdapter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("campBudget", "campExpenses")
fun bindTextViewToRemainingMoney(textView: TextView, campBudget: Double?, campExpenses: Double?) {
    if (campBudget != null && campExpenses != null) {
        val remainingMoney = campBudget - campExpenses
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.currency = Currency.getInstance("PLN")

        textView.text = format.format(remainingMoney)
    }
}