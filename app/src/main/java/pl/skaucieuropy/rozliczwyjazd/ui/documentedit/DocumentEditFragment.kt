package pl.skaucieuropy.rozliczwyjazd.ui.documentedit

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.constants.AMOUNT_FORMAT
import pl.skaucieuropy.rozliczwyjazd.database.ReckoningDatabase
import pl.skaucieuropy.rozliczwyjazd.databinding.FragmentDocumentEditBinding
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import pl.skaucieuropy.rozliczwyjazd.ui.documentedit.adapter.NoFilterArrayAdapter
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
        Log.v("DocumentEditFragment", "onCreateView called")
        setupViewModel()
        setupBinding(inflater)

        setupDocument()

        setupEdtTexts()
        setupExposedDropdownMenus()
        setupDatePicker()

        setupObservers()
        setHasOptionsMenu(true)
        binding.container.requestFocus()

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
        viewModel.document.value?.id?.value = documentId
        if (documentId == 0L) {
            if (!viewModel.documentHasChanged) {
                setDefaultStringValues()
            }
        } else if (!viewModel.documentHasLoadedFromDb) {
            viewModel.getDocumentFromDb()
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
        val typeAdapter = NoFilterArrayAdapter(
            requireContext(),
            R.layout.simple_list_item,
            resources.getStringArray(R.array.document_types),
        )
        binding.typeAutoComplete.setAdapter(typeAdapter)

        val categoryAdapter = NoFilterArrayAdapter(
            requireContext(),
            R.layout.simple_list_item,
            resources.getStringArray(R.array.document_categories),
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

    private fun setupObservers() {
        viewModel.navigateToDocuments.observe(viewLifecycleOwner) { navigate ->
            navigate?.let {
                if (navigate) {
                    navToDocuments()
                    viewModel.navigateToDocumentsCompleted()
                }
            }
        }
        viewModel.document.observe(viewLifecycleOwner) {
            it?.let {
                if (it != viewModel.originalDocument) {
                    viewModel.documentHasChanged = true
                }
            }
        }
    }

    private fun navToDocuments() {
        findNavController().navigate(DocumentEditFragmentDirections.actionDocumentEditFragmentToDocumentsFragment())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.document_edit_overflow_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (viewModel.document.value?.id?.value == 0L) {
            menu.findItem(R.id.delete_document).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                viewModel.saveDocument()
                return true
            }
            R.id.delete_document -> {
                showDeleteConfirmationDialog()
                return true
            }
            R.id.cancel_document_changes -> {
                navToDocuments()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setMessage(getString(R.string.delete_document_dialog_msg))
            setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.deleteDocument()
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog?.dismiss()
            }
            show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.v("DocumentEditFragment", "onAttach called")

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.saveDocument()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("DocumentEditFragment", "onCreate called")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.v("DocumentEditFragment", "onViewCreated called")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.v("DocumentEditFragment", "onViewStateRestored called")
    }

    override fun onStart() {
        super.onStart()
        Log.v("DocumentEditFragment", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.v("DocumentEditFragment", "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.v("DocumentEditFragment", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.v("DocumentEditFragment", "onStop called")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.v("DocumentEditFragment", "onSaveInstanceState called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.v("DocumentEditFragment", "onDestroyView called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("DocumentEditFragment", "onDestroy called")
    }
}