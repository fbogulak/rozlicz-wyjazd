package pl.skaucieuropy.rozliczwyjazd.ui.documentedit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.constants.ACCOMMODATION
import pl.skaucieuropy.rozliczwyjazd.constants.AMOUNT_FORMAT
import pl.skaucieuropy.rozliczwyjazd.constants.BUSINESS_TRIP_ORDER
import pl.skaucieuropy.rozliczwyjazd.constants.INSURANCE
import pl.skaucieuropy.rozliczwyjazd.constants.INSURANCE_POLICY
import pl.skaucieuropy.rozliczwyjazd.constants.SETTLEMENT_OF_DELEGATION
import pl.skaucieuropy.rozliczwyjazd.constants.SIMPLIFIED_INVOICE
import pl.skaucieuropy.rozliczwyjazd.constants.STATEMENT
import pl.skaucieuropy.rozliczwyjazd.constants.TICKET
import pl.skaucieuropy.rozliczwyjazd.constants.TICKETS
import pl.skaucieuropy.rozliczwyjazd.databinding.FragmentDocumentEditBinding
import pl.skaucieuropy.rozliczwyjazd.ui.base.BaseFragment
import pl.skaucieuropy.rozliczwyjazd.ui.documentedit.adapter.NoFilterArrayAdapter
import pl.skaucieuropy.rozliczwyjazd.utils.CurrencyInputFilter
import pl.skaucieuropy.rozliczwyjazd.utils.toDoubleOrZero
import java.util.Date

class DocumentEditFragment : BaseFragment() {

    override val viewModel: DocumentEditViewModel by viewModel()
    private lateinit var binding: FragmentDocumentEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupBinding(inflater)

        setupDocument()

        setupEdtTexts()
        setupExposedDropdownMenus()

        setupObservers()
        setupListeners()
        setupMenu()
        binding.container.requestFocus()

        return binding.root
    }

    private fun setupBinding(inflater: LayoutInflater) {
        binding = FragmentDocumentEditBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupDocument() {
        val documentId = DocumentEditFragmentArgs.fromBundle(requireArguments()).argDocumentId
        viewModel.document.id = documentId
        if (documentId == 0L) {
            viewModel.setupDatePicker()
            if (hasNotDefaultStringValues()) {
                setDefaultStringValues()
            }
        } else if (!viewModel.documentHasLoadedFromDb) {
            viewModel.getDocumentFromDb()
        }
    }

    private fun hasNotDefaultStringValues() = viewModel.documentType.value.isNullOrEmpty() &&
            viewModel.documentCategory.value.isNullOrEmpty()

    private fun setDefaultStringValues() {
        viewModel.documentType.value = getString(R.string.vat_invoice)
        viewModel.documentCategory.value = getString(R.string.groceries)
    }

    private fun setupEdtTexts() {
        binding.amountEdit.apply {
            filters = arrayOf(CurrencyInputFilter())
            setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    setText(AMOUNT_FORMAT.format(text.toString().toDoubleOrZero()))
                }
            }
        }
        binding.amountFromOnePercentEdit.apply {
            filters = arrayOf(CurrencyInputFilter())
            setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && !text.isNullOrBlank()) {
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
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.document_date))
                .setSelection(viewModel.documentDate.value?.time)
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        datePicker.addOnPositiveButtonClickListener {
            viewModel.documentDate.value = Date(it)
        }

        binding.dateEdit.setOnClickListener {
            val dialog = datePicker.dialog
            if (dialog == null || !dialog.isShowing) {
                datePicker.show(parentFragmentManager, "DocumentDatePicker")
            }
        }
    }

    private fun setupObservers() {
        viewModel.setupDatePicker.observe(viewLifecycleOwner) { setup ->
            setup?.let {
                if (setup) {
                    setupDatePicker()
                    viewModel.setupDatePickerCompleted()
                }
            }
        }
        viewModel.documentAmount.observe(viewLifecycleOwner) {
            checkAmountWithType()
            checkAmountFromOnePercent()
        }
        viewModel.documentType.observe(viewLifecycleOwner) {
            checkAmountWithType()
        }
        viewModel.documentAmountFromOnePercent.observe(viewLifecycleOwner) {
            checkAmountFromOnePercent()
        }
        viewModel.documentType.observe(viewLifecycleOwner) {
            if (binding.typeTextField.hasFocus())
                when (it) {
                    TICKET -> viewModel.documentCategory.value = TICKETS
                    BUSINESS_TRIP_ORDER -> viewModel.documentCategory.value =
                        SETTLEMENT_OF_DELEGATION

                    INSURANCE_POLICY -> viewModel.documentCategory.value = INSURANCE
                    STATEMENT -> viewModel.documentCategory.value = ACCOMMODATION
                }
        }
    }

    private fun checkAmountWithType() {
        val amount = viewModel.documentAmount.value
        val type = viewModel.documentType.value
        if (amount != null && type != null && amount > 450 && type == SIMPLIFIED_INVOICE) {
            binding.amountTextField.error = getString(R.string.amount_over_450)
        } else {
            binding.amountTextField.error = null
        }
    }

    private fun checkAmountFromOnePercent() {
        val amount = viewModel.documentAmount.value
        val amountFromOnePercent = viewModel.documentAmountFromOnePercent.value
        if (amountFromOnePercent != null && amount != null && amountFromOnePercent > amount) {
            binding.amountFromOnePercentTextField.error =
                getString(R.string.amount_from_one_percent_over_amount)
        } else {
            binding.amountFromOnePercentTextField.error = null
        }
    }

    private fun setupListeners() {
        binding.infoButton.setOnClickListener {
            showInfoDialog()
        }
        binding.saveDocumentFab.setOnClickListener {
            saveDocument()
        }
    }

    private fun showInfoDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.category)
            .setMessage(getString(R.string.document_category_info_message))
            .setIcon(R.drawable.ic_info)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog?.dismiss()
            }
            .show()
    }

    private fun saveDocument() {
        if (validateDocument()) {
            viewModel.saveDocument()
        }
    }

    private fun validateDocument(): Boolean {
        var validationPassed = true
        viewModel.updateDocumentProperties()
        viewModel.document.apply {
            if (type == SIMPLIFIED_INVOICE && amount > 450) {
                showSimplifiedInvoiceErrorDialog()
                validationPassed = false
            }
            if (amountFromOnePercent != null && amountFromOnePercent!! > amount) {
                showOnePercentErrorDialog()
                validationPassed = false
            }
        }
        return validationPassed
    }

    private fun showSimplifiedInvoiceErrorDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.should_save_incorrect_document_title)
            .setMessage(getString(R.string.this_is_not_simplified_invoice_error_message))
            .setIcon(R.drawable.ic_production_quantity_limits)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.saveDocument()
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showOnePercentErrorDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.amount_from_one_percent_over_amount)
            .setMessage(getString(R.string.amount_from_one_percent_over_amount_error_message))
            .setIcon(R.drawable.ic_error)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.document_edit_overflow_menu, menu)
            }

            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
                if (viewModel.document.id == 0L) {
                    menu.findItem(R.id.delete_document).isVisible = false
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        saveDocument()
                        return true
                    }

                    R.id.delete_document -> {
                        showDeleteConfirmationDialog()
                        return true
                    }

                    R.id.cancel_document_changes -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.changes_discarded),
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.navigateToDocuments()
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.delete_document_dialog_msg))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.deleteDocument()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog?.dismiss()
            }
            .show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveDocument()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}