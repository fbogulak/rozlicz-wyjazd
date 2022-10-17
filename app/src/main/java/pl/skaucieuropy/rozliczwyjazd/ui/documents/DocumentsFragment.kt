package pl.skaucieuropy.rozliczwyjazd.ui.documents

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.databinding.FragmentDocumentsBinding
import pl.skaucieuropy.rozliczwyjazd.ui.base.BaseFragment
import pl.skaucieuropy.rozliczwyjazd.ui.documents.adapter.DocumentsListAdapter


class DocumentsFragment : BaseFragment(), SearchView.OnQueryTextListener,
    MenuItem.OnActionExpandListener {

    override val viewModel: DocumentsViewModel by viewModel()
    private var _binding: FragmentDocumentsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupBinding(inflater, container)

        setupRecycler()
        setupObservers()
        setHasOptionsMenu(true)

        return binding.root
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
                viewModel.navigateToDocumentEdit(
                    document.id,
                    getString(R.string.edit_document_title)
                )
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
        viewModel.documents.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isEmpty()) {
                    binding.documentsRecycler.visibility = View.GONE
                    binding.documentsEmptyView.apply {
                        setText(
                            if (viewModel.isSearching)
                                R.string.documents_empty_view_if_searching
                            else
                                R.string.documents_empty_view_hint
                        )
                        visibility = View.VISIBLE
                    }
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
        viewModel.isSearching = true
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
        return true
    }

    override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
        viewModel.searchQuery.value = ""
        viewModel.isSearching = false
        return true
    }
}