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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MenuRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.databinding.FragmentCampsBinding
import pl.skaucieuropy.rozliczwyjazd.models.domain.Camp
import pl.skaucieuropy.rozliczwyjazd.ui.base.BaseFragment
import pl.skaucieuropy.rozliczwyjazd.ui.camps.adapter.CampsListAdapter
import java.io.*

class CampsFragment : BaseFragment() {

    override val viewModel: CampsViewModel by viewModel()
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
            } else {
                binding.progressBar.hide()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupBinding(inflater, container)

        setupRecycler()
        setupObservers()

        return binding.root
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
                viewModel.navigateToCampEdit(
                    camp.id,
                    getString(R.string.edit_camp_title)
                )

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
                    viewModel.changeActiveCamp(camp.id)
                    true
                }
                R.id.export -> {
                    viewModel.exportToCsv(camp)
                    binding.progressBar.show()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    private fun setupObservers() {
        viewModel.createExportFile.observe(viewLifecycleOwner) {
            it?.let {
                createFile(it.name)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        val fileBody = viewModel.createExportFile.value?.body ?: ""
        try {
            contentResolver.openFileDescriptor(uri, "w")?.use {
                FileOutputStream(it.fileDescriptor).use {
                    BufferedWriter(OutputStreamWriter(it, "windows-1250"))
                        .append(fileBody)
                        .close()
                }
            }
            Snackbar.make(
                binding.root,
                getString(R.string.csv_file_saved),
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(getString(R.string.send)) { sendFile(uri) }
                .show()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            viewModel.createExportFileCompleted()
            binding.progressBar.hide()
        }
    }

    private fun sendFile(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }

    }
}