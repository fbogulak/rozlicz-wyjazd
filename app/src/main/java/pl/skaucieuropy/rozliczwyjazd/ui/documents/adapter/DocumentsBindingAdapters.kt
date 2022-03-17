package pl.skaucieuropy.rozliczwyjazd.ui.documents.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.constants.STATEMENT
import pl.skaucieuropy.rozliczwyjazd.models.domain.Document
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("documentsListData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Document>?) {
    val adapter = recyclerView.adapter as DocumentsListAdapter
    adapter.submitList(data)
}

@BindingAdapter("documentAmount")
fun bindTextViewToAmount(textView: TextView, amount: Double?) {
    val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
    format.currency = Currency.getInstance("PLN")

    textView.text = format.format(amount)
}

@BindingAdapter("documentDate")
fun bindTextViewToDate(textView: TextView, date: Date) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    textView.text = dateFormat.format(date)
}

@BindingAdapter("documentType")
fun bindTextViewToType(textView: TextView, type: String?) {
    type?.let {
        textView.text = if (type == STATEMENT) type else textView.resources.getString(
            R.string.document_type_format,
            type
        )
    }
}