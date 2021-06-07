package pl.skaucieuropy.rozliczwyjazd.ui.documents.adapter

import android.widget.ListAdapter
import pl.skaucieuropy.rozliczwyjazd.model.document.Document

class DocumentsListAdapter(private val clickListener: DocumentListener) :
    ListAdapter<Document, DocumentsListAdapter.DocumentViewHolder>(DocumentDiffCallback) {

    class DocumentViewHolder(private var binding: ) {

    }

    class DocumentListener(val clickListener: (document: Document) -> Unit) {
        fun onClick(document: Document) = clickListener(document)
    }
}