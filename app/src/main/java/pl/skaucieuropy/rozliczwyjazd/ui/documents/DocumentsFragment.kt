package pl.skaucieuropy.rozliczwyjazd.ui.documents

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.database.ReckoningDatabase
import pl.skaucieuropy.rozliczwyjazd.databinding.FragmentDocumentsBinding
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import pl.skaucieuropy.rozliczwyjazd.ui.documents.adapter.DocumentsListAdapter


class DocumentsFragment : Fragment(), SearchView.OnQueryTextListener,
    MenuItem.OnActionExpandListener {

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
        setupViewModel()
        setupBinding(inflater, container)

        setupRecycler()
        setupObservers()
        setHasOptionsMenu(true)

        return binding.root
    }

    private fun setupViewModel() {
        val database = ReckoningDatabase.getInstance(requireContext())
        val repository = ReckoningRepository(database)
        viewModel =
            ViewModelProvider(
                this,
                DocumentsViewModelFactory(repository)
            ).get(DocumentsViewModel::class.java)
    }

    private fun setupBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        _binding = FragmentDocumentsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun setupRecycler() {
        binding.documentsRecycler.adapter =
            DocumentsListAdapter(DocumentsListAdapter.DocumentListener { document ->
                document.id.value?.let {
                    navToDocumentEdit(
                        it,
                        getString(R.string.edit_document_title)
                    )
                }
            })
        binding.documentsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.addDocumentFab.apply {
                    if (dy > 0 && visibility == View.VISIBLE) {
                        hide()
                    } else if (dy < 0 && visibility != View.VISIBLE) {
                        show()
                    }
                }
            }
        })
    }

    private fun setupObservers() {
        viewModel.navigateToDocumentEdit.observe(viewLifecycleOwner) { navigate ->
            navigate?.let {
                if (navigate) {
                    navToDocumentEdit(0, getString(R.string.add_document_title))
                    viewModel.navigateToDocumentEditCompleted()
                }
            }
        }
        viewModel.documents.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isEmpty()) {
                    binding.documentsRecycler.visibility = View.GONE
                    binding.documentsEmptyView.visibility = View.VISIBLE
                } else {
                    binding.documentsRecycler.visibility = View.VISIBLE
                    binding.documentsEmptyView.visibility = View.GONE
                }
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.documents_overflow_menu, menu)
        val searchItem = menu.findItem(R.id.search_documents)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchItem.setOnActionExpandListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        viewModel.searchQuery.value = query
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        viewModel.searchQuery.value = ""
        return true
    }
}