package pl.skaucieuropy.rozliczwyjazd.ui.documentedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.constants.AMOUNT_FORMAT
import pl.skaucieuropy.rozliczwyjazd.database.ReckoningDatabase
import pl.skaucieuropy.rozliczwyjazd.databinding.FragmentDocumentEditBinding
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import pl.skaucieuropy.rozliczwyjazd.utils.CurrencyInputFilter
import pl.skaucieuropy.rozliczwyjazd.utils.toDoubleOrZero
import java.util.*

class DocumentEditFragment : Fragment() {

    private lateinit var viewModel: DocumentEditViewModel
    private lateinit var binding: FragmentDocumentEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupViewModel()
        setupBinding(inflater)

        setupDocument()

        setupEdtTexts()
        setupExposedDropdownMenus()
        setupDatePicker()

        return binding.root
    }

    private fun setupViewModel() {
        val database = ReckoningDatabase.getInstance(requireContext())
        val repository = ReckoningRepository(database)
        viewModel = ViewModelProvider(this, DocumentEditViewModelFactory(repository)).get(
            DocumentEditViewModel::class.java
        )
    }

    private fun setupBinding(inflater: LayoutInflater) {
        binding = FragmentDocumentEditBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupDocument() {
        val documentId = DocumentEditFragmentArgs.fromBundle(requireArguments()).argDocumentId
        if (documentId == 0L) {
            setDefaultStringValues()
        } else {
            viewModel.getDocumentFromDb(documentId)
        }
    }

    private fun setDefaultStringValues() {
        viewModel.document.value?.type?.value = getString(R.string.vat_invoice)
        viewModel.document.value?.category?.value = getString(R.string.groceries)
    }

    private fun setupEdtTexts() {
        binding.dateEdit.keyListener = null

        binding.amountEdit.apply {
            filters = arrayOf(CurrencyInputFilter())
            setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    setText(AMOUNT_FORMAT.format(text.toString().toDoubleOrZero()))
                }
            }
        }
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

    private fun setupDatePicker() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.buy_date))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        datePicker.addOnPositiveButtonClickListener {
            viewModel.document.value?.date?.value = Date(it)
        }

        binding.dateEdit.apply {
            setOnClickListener {
                datePicker.show(parentFragmentManager, "tag")
            }
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus)
                    datePicker.show(parentFragmentManager, "tag")
            }
        }
    }
}