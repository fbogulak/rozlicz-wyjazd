package pl.skaucieuropy.rozliczwyjazd.ui.camps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.database.ReckoningDatabase
import pl.skaucieuropy.rozliczwyjazd.databinding.FragmentCampsBinding
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import pl.skaucieuropy.rozliczwyjazd.ui.camps.adapter.CampsListAdapter
import pl.skaucieuropy.rozliczwyjazd.ui.documents.DocumentsFragmentDirections

class CampsFragment : Fragment() {

    private lateinit var viewModel: CampsViewModel
    private var _binding: FragmentCampsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupViewModel()
        setupBinding(inflater, container)

        setupRecycler()
        setupObservers()

        return binding.root
    }

    private fun setupViewModel() {
        val database = ReckoningDatabase.getInstance(requireContext())
        val repository = ReckoningRepository(database)
        viewModel =
            ViewModelProvider(
                this,
                CampsViewModelFactory(repository)
            ).get(CampsViewModel::class.java)
    }

    private fun setupBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        _binding = FragmentCampsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun setupRecycler() {
        binding.campsRecycler.adapter =
            CampsListAdapter(CampsListAdapter.CampListener { camp ->
                camp.id.value?.let {
                    navToCampEdit(
                        it,
                        getString(R.string.edit_camp_title)
                    )
                }
            }, CampsListAdapter.MenuListener { v, camp ->
                showMenu(v, R.menu.camp_popup_menu, camp)
            })
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int, camp: Camp) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.choose_as_active -> {
                    viewModel.changeActiveCamp(camp.id.value)
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    private fun setupObservers() {
        viewModel.navigateToCampEdit.observe(viewLifecycleOwner) { navigate ->
            navigate?.let {
                if (navigate) {
                    navToCampEdit(0, getString(R.string.add_camp_title))
                    viewModel.navigateToCampEditCompleted()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navToCampEdit(campId: Long, destinationLabel: String) {
        findNavController().navigate(
            CampsFragmentDirections.actionCampsFragmentToCampEditFragment(
                campId, destinationLabel
            )
        )
    }
}