package pl.skaucieuropy.rozliczwyjazd.ui.camps.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import pl.skaucieuropy.rozliczwyjazd.ui.camps.adapter.CampsListAdapter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("campsListData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Camp>?) {
    val adapter = recyclerView.adapter as CampsListAdapter
    adapter.submitList(data)
}

@BindingAdapter("campDate")
fun bindTextViewToDate(textView: TextView, date: Date) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    textView.text = dateFormat.format(date)
}

@BindingAdapter("campIsActive")
fun bindImageViewToIsActive(imageView: ImageView, isActive: Boolean?) {
    isActive?.let {
        imageView.visibility = if (it) View.VISIBLE else View.INVISIBLE
    }
}