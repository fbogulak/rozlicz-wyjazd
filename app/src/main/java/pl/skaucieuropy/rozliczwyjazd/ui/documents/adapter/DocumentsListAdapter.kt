package pl.skaucieuropy.rozliczwyjazd.ui.documents.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import pl.skaucieuropy.rozliczwyjazd.databinding.DocumentListItemBinding
import pl.skaucieuropy.rozliczwyjazd.models.domain.Document

class DocumentsListAdapter(private val clickListener: DocumentListener) :
    ListAdapter<Document, DocumentsListAdapter.DocumentViewHolder>(DocumentDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        return DocumentViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val document = getItem(position)
        holder.bind(document, clickListener)
    }

    companion object DocumentDiffCallback : DiffUtil.ItemCallback<Document>() {
        override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
            return oldItem.id.value == newItem.id.value
        }

        override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
            return oldItem == newItem
        }
    }

    class DocumentViewHolder(private var binding: DocumentListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(document: Document, clickListener: DocumentListener) {
            binding.document = document
            binding.onClickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): DocumentViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DocumentListItemBinding.inflate(layoutInflater, parent, false)
                return DocumentViewHolder(binding)
            }
        }
    }

    class DocumentListener(val clickListener: (document: Document) -> Unit) {
        fun onClick(document: Document) = clickListener(document)
    }
}