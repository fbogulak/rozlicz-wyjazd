package pl.skaucieuropy.rozliczwyjazd.ui.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.database.ReckoningDatabase
import pl.skaucieuropy.rozliczwyjazd.databinding.FragmentDocumentsBinding
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import pl.skaucieuropy.rozliczwyjazd.ui.documents.adapter.DocumentsListAdapter

class DocumentsFragment : Fragment() {

    private lateinit var viewModel: DocumentsViewModel
    private var _binding: FragmentDocumentsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val database = ReckoningDatabase.getInstance(requireContext())
        val repository = ReckoningRepository(database)
        viewModel =
            ViewModelProvider(
                this,
                DocumentsViewModelFactory(repository)
            ).get(DocumentsViewModel::class.java)

        _binding = FragmentDocumentsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.documentsRecycler.adapter =
            DocumentsListAdapter(DocumentsListAdapter.DocumentListener { document ->
                document.id.value?.let {
                    navToDocumentEdit(
                        it,
                        getString(R.string.edit_document_title)
                    )
                }
            })

        viewModel.navigateToDocumentEdit.observe(viewLifecycleOwner) { navigate ->
            navigate?.let {
                if (navigate) {
                    navToDocumentEdit(0, getString(R.string.add_document_title))
                    viewModel.navigateToDocumentEditCompleted()
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navToDocumentEdit(documentId: Long, destinationLabel: String) {
        findNavController().navigate(
            DocumentsFragmentDirections.actionDocumentsFragmentToDocumentEditFragment(
                documentId, destinationLabel
            )
        )
    }
}