package pl.skaucieuropy.rozliczwyjazd.ui.documentedit.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.textfield.TextInputLayout
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.constants.AMOUNT_FORMAT
import pl.skaucieuropy.rozliczwyjazd.constants.STATEMENT
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

@BindingAdapter("visibilityByType")
fun bindViewVisibilityToType(view: View, type: String?) {
    type?.let {
        if (it == STATEMENT) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("category")
fun bindHelperTextToCategory(view: TextInputLayout, category: String?) {
    view.helperText =
        if (category == "Art. na obóz") view.context.getString(R.string.document_category_helper_text) else null
}