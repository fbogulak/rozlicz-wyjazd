package pl.skaucieuropy.rozliczwyjazd.ui.documentedit

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.databinding.FragmentDocumentEditBinding

class DocumentEditFragment : Fragment() {

    private lateinit var viewModel: DocumentEditViewModel
    private lateinit var binding: FragmentDocumentEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(DocumentEditViewModel::class.java)
        binding = FragmentDocumentEditBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        setupExposedDropdownMenus()

        return binding.root
    }

    private fun setupExposedDropdownMenus() {
        val typeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.document_types,
            R.layout.simple_list_item
        )
        binding.typeAutoComplete.setAdapter(typeAdapter)

        val categoryAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.document_categories,
            R.layout.simple_list_item
        )
        binding.categoryAutoComplete.setAdapter(categoryAdapter)
    }
}