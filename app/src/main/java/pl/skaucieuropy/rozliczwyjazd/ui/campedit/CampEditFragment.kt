package pl.skaucieuropy.rozliczwyjazd.ui.campedit

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.constants.AMOUNT_FORMAT
import pl.skaucieuropy.rozliczwyjazd.database.ReckoningDatabase
import pl.skaucieuropy.rozliczwyjazd.databinding.FragmentCampEditBinding
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import pl.skaucieuropy.rozliczwyjazd.utils.CurrencyInputFilter
import pl.skaucieuropy.rozliczwyjazd.utils.hideKeyboard
import pl.skaucieuropy.rozliczwyjazd.utils.toDoubleOrZero
import java.util.*

class CampEditFragment : Fragment() {

    private lateinit var viewModel: CampEditViewModel
    private lateinit var binding: FragmentCampEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupViewModel()
        setupBinding(inflater)

        setupCamp()

        setupEdtTexts()

        setupObservers()
        setHasOptionsMenu(true)
        binding.container.requestFocus()

        return binding.root
    }

    private fun setupViewModel() {
        val database = ReckoningDatabase.getInstance(requireContext())
        val repository = ReckoningRepository(database)
        viewModel = ViewModelProvider(this, CampEditViewModelFactory(repository)).get(
            CampEditViewModel::class.java
        )
    }

    private fun setupBinding(inflater: LayoutInflater) {
        binding = FragmentCampEditBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupCamp() {
        val campId = CampEditFragmentArgs.fromBundle(requireArguments()).argCampId
        viewModel.camp.value?.id?.value = campId
        if (campId == 0L) {
            viewModel.setupDatePicker()
        } else if (!viewModel.campHasLoadedFromDb) {
            viewModel.getCampFromDb()
        }
    }

    private fun setupEdtTexts() {
        binding.dateEdit.keyListener = null

        binding.budgetEdit.apply {
            filters = arrayOf(CurrencyInputFilter())
            setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    setText(AMOUNT_FORMAT.format(text.toString().toDoubleOrZero()))
                }
            }
        }
    }

    private fun setupDatePicker() {
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(getString(R.string.camp_date))
                .setSelection(
                    Pair(
                        viewModel.camp.value?.startDate?.value?.time,
                        viewModel.camp.value?.endDate?.value?.time
                    )
                ).build()

        dateRangePicker.addOnPositiveButtonClickListener {
            viewModel.camp.value?.apply {
                startDate.value = Date(it.first)
                endDate.value = Date(it.second)
            }
        }

        binding.dateEdit.apply {
            setOnClickListener {
                dateRangePicker.show(parentFragmentManager, "tag")
            }
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus)
                    dateRangePicker.show(parentFragmentManager, "tag")
            }
        }
    }

    private fun setupObservers() {
        viewModel.navigateToCamps.observe(viewLifecycleOwner) { navigate ->
            navigate?.let {
                if (navigate) {
                    navToCamps()
                    viewModel.navigateToCampsCompleted()
                }
            }
        }
        viewModel.setupDatePicker.observe(viewLifecycleOwner) { setup ->
            setup?.let {
                if (setup) {
                    setupDatePicker()
                    viewModel.setupDatePickerCompleted()
                }
            }
        }
        viewModel.showToast.observe(viewLifecycleOwner) {
            it?.content?.let { content ->
                val message = when (content) {
                    is String -> content
                    is Int -> getString(content)
                    else -> return@let
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                viewModel.showToastCompleted()
            }
        }
    }

    private fun navToCamps() {
        hideKeyboard()
        findNavController().navigate(CampEditFragmentDirections.actionCampEditFragmentToCampsFragment())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.camp_edit_overflow_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (viewModel.camp.value?.id?.value == 0L) {
            menu.findItem(R.id.delete_camp).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                viewModel.saveCamp()
                return true
            }
            R.id.delete_camp -> {
                showDeleteConfirmationDialog()
                return true
            }
            R.id.cancel_camp_changes -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.changes_discarded),
                    Toast.LENGTH_SHORT
                ).show()
                navToCamps()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.delete_camp_dialog_msg))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.deleteCamp()
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
                viewModel.saveCamp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}