package pl.skaucieuropy.rozliczwyjazd.ui.camps

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MenuRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.database.ReckoningDatabase
import pl.skaucieuropy.rozliczwyjazd.databinding.FragmentCampsBinding
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import pl.skaucieuropy.rozliczwyjazd.ui.camps.adapter.CampsListAdapter
import java.io.*

class CampsFragment : Fragment() {

    private lateinit var viewModel: CampsViewModel
    private var _binding: FragmentCampsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it?.data?.data?.also { uri ->
                    alterDocument(uri)
                }
            }
        }

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
        binding.campsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.addCampFab.apply {
                    if (dy > 0 && visibility == View.VISIBLE) {
                        hide()
                    } else if (dy < 0 && visibility != View.VISIBLE) {
                        show()
                    }
                }
            }
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
                R.id.export -> {
                    viewModel.exportToCsv(camp)
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
        viewModel.createExportFile.observe(viewLifecycleOwner) {
            it?.let {
                createFile(it.first)
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

    private fun createFile(fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        activityResultLauncher.launch(intent)
    }

    private fun alterDocument(uri: Uri) {
        val contentResolver = requireContext().contentResolver
        val fileContent = viewModel.createExportFile.value?.second ?: ""
        try {
            contentResolver.openFileDescriptor(uri, "w")?.use {
                FileOutputStream(it.fileDescriptor).use {
                    BufferedWriter(OutputStreamWriter(it, "windows-1250"))
                        .append(fileContent)
                        .close()
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            viewModel.createExportFileCompleted()
        }
    }
}