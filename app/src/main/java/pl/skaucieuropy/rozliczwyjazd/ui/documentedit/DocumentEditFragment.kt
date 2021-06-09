package pl.skaucieuropy.rozliczwyjazd.ui.documentedit

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.skaucieuropy.rozliczwyjazd.R

class DocumentEditFragment : Fragment() {

    companion object {
        fun newInstance() = DocumentEditFragment()
    }

    private lateinit var viewModel: DocumentEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_document_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DocumentEditViewModel::class.java)
        // TODO: Use the ViewModel
    }

}