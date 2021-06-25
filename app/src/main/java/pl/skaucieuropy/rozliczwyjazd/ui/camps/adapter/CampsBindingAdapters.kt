package pl.skaucieuropy.rozliczwyjazd.ui.camps.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("campsListData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Camp>?) {
    val adapter = recyclerView.adapter as CampsListAdapter
    adapter.submitList(data)
}

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

@BindingAdapter("campIsActive")
fun bindImageViewToIsActive(imageView: ImageView, isActive: Boolean?) {
    isActive?.let {
        imageView.visibility = if (it) View.VISIBLE else View.INVISIBLE
    }
}