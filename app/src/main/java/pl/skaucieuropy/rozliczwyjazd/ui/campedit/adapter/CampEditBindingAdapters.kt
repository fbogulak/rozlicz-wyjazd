package pl.skaucieuropy.rozliczwyjazd.ui.campedit.adapter

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.constants.AMOUNT_FORMAT
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import pl.skaucieuropy.rozliczwyjazd.utils.toDoubleOrZero
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("startDate", "endDate")
fun bindTextViewToCampDate(textView: TextView, startDate: Date?, endDate: Date?) {
    if (startDate != null && endDate != null) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        textView.text = textView.context.getString(
            R.string.camp_date_format,
            dateFormat.format(startDate),
            dateFormat.format(endDate)
        )
    }
}

@BindingAdapter("budget")
fun setBudget(view: EditText, newValue: Double?) {
    newValue?.let {
        if (view.text.toString().toDoubleOrZero() != newValue) {
            view.setText(AMOUNT_FORMAT.format(it))
        }
    }
}

@InverseBindingAdapter(attribute = "budget")
fun getBudget(view: EditText): Double {
    return view.text.toString().toDoubleOrZero()
}

@BindingAdapter("budgetAttrChanged")
fun setBudgetListeners(
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