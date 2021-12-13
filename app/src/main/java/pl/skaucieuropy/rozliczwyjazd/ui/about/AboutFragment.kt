package pl.skaucieuropy.rozliczwyjazd.ui.about

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pl.skaucieuropy.rozliczwyjazd.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {
    private lateinit var viewModel: AboutViewModel
    private var _binding: FragmentAboutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupViewModel()
        setupBinding(inflater)

        return binding.root
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(AboutViewModel::class.java)
    }

    private fun setupBinding(inflater: LayoutInflater) {
        _binding = FragmentAboutBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}