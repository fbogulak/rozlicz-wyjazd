package pl.skaucieuropy.rozliczwyjazd.ui.documentedit.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class NoFilterArrayAdapter<T>(context: Context, textViewResourceId: Int, objects: Array<T>) :
    ArrayAdapter<T>(context, textViewResourceId, objects) {
    private val filter: Filter = NoFilter()
    var items: Array<T> = objects
    override fun getFilter(): Filter {
        return filter
    }

    private inner class NoFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val result = FilterResults()
            result.values = items
            result.count = items.size
            return result
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }
}