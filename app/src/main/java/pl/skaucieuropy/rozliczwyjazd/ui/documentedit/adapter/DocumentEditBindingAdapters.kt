package pl.skaucieuropy.rozliczwyjazd.ui.documentedit.adapter

import android.text.Editable
import android.text.TextWatcher
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import pl.skaucieuropy.rozliczwyjazd.constants.AMOUNT_FORMAT
import pl.skaucieuropy.rozliczwyjazd.utils.toDoubleOrZero
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("date")
fun bindTextViewToDate(textView: TextView, date: Date?) {
    date?.let {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        textView.text = dateFormat.format(it)
    }
}

@BindingAdapter("amount")
fun setAmount(view: EditText, newValue: Double?) {
    newValue?.let {
        if (view.text.toString().toDoubleOrZero() != newValue) {
            view.setText(AMOUNT_FORMAT.format(it))
        }
    }
}

@InverseBindingAdapter(attribute = "amount")
fun getAmount(view: EditText): Double {
    return view.text.toString().toDoubleOrZero()
}

@BindingAdapter("amountAttrChanged")
fun setAmountListeners(
    view: EditText,
    attrChange: InverseBindingListener
) {
    view.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            attrChange.onChange()
        }

        override fun afterTextChanged(s: Editable?) {
        }
    })
}