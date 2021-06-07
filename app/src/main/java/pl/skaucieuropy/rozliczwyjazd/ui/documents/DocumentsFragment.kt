package pl.skaucieuropy.rozliczwyjazd.ui.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pl.skaucieuropy.rozliczwyjazd.databinding.FragmentDocumentsBinding
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
        viewModel =
            ViewModelProvider(this).get(DocumentsViewModel::class.java)

        _binding = FragmentDocumentsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.documentsRecycler.adapter =
            DocumentsListAdapter(DocumentsListAdapter.DocumentListener {

            })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}