package pl.skaucieuropy.rozliczwyjazd.ui.documents

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.databinding.FragmentDocumentsBinding
import pl.skaucieuropy.rozliczwyjazd.ui.base.BaseFragment
import pl.skaucieuropy.rozliczwyjazd.ui.documents.adapter.DocumentsListAdapter
import pl.skaucieuropy.rozliczwyjazd.utils.DocumentsOrderBy


class DocumentsFragment : BaseFragment(), SearchView.OnQueryTextListener,
    MenuItem.OnActionExpandListener {

    override val viewModel: DocumentsViewModel by viewModel()
    private var _binding: FragmentDocumentsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var query: String? = null
    private var orderBy = DocumentsOrderBy.DATE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupBinding(inflater, container)

        setupRecycler()
        setupObservers()
        setupMenu()

        getDocuments()

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

    private fun getDocuments() {
        val sharedPref = requireActivity().getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        orderBy = DocumentsOrderBy.valueOf(
            sharedPref.getString(
                getString(R.string.saved_documents_order_by_key),
                "DATE"
            ) ?: "DATE"
        )
        viewModel.getDocuments(null, orderBy)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            @SuppressLint("RestrictedApi")
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.documents_overflow_menu, menu)
                val searchItem = menu.findItem(R.id.search_documents)
                val searchView = searchItem.actionView as SearchView
                searchView.setOnQueryTextListener(this@DocumentsFragment)
                searchItem.setOnActionExpandListener(this@DocumentsFragment)

                if (menu is MenuBuilder) {
                    menu.setOptionalIconsVisible(true)
                }
            }

            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
                when (orderBy) {
                    DocumentsOrderBy.DATE -> {
                        menu.findItem(R.id.sort_by_document_date).icon =
                            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_ok)
                        menu.findItem(R.id.sort_by_creation_timestamp).icon = null
                    }
                    DocumentsOrderBy.CREATION_TIMESTAMP -> {
                        menu.findItem(R.id.sort_by_document_date).icon = null
                        menu.findItem(R.id.sort_by_creation_timestamp).icon =
                            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_ok)
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.sort_by_document_date -> {
                        sortDocuments(DocumentsOrderBy.DATE)
                        return true
                    }
                    R.id.sort_by_creation_timestamp -> {
                        sortDocuments(DocumentsOrderBy.CREATION_TIMESTAMP)
                        return true
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun sortDocuments(orderBy: DocumentsOrderBy) {
        this.orderBy = orderBy
        val sharedPref = requireActivity().getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        with(sharedPref?.edit()) {
            this?.putString(
                getString(R.string.saved_documents_order_by_key),
                orderBy.name
            )
            this?.apply()
        }
        viewModel.getDocuments(query, orderBy)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        this.query = query
        viewModel.getDocuments(query, orderBy)
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
        query = null
        viewModel.getDocuments(query, orderBy)
        viewModel.isSearching = false
        return true
    }
}