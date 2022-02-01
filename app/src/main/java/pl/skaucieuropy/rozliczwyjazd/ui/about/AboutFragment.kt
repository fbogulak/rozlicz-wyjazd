package pl.skaucieuropy.rozliczwyjazd.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.skaucieuropy.rozliczwyjazd.databinding.FragmentAboutBinding
import pl.skaucieuropy.rozliczwyjazd.ui.base.BaseFragment

class AboutFragment : BaseFragment() {
    override val viewModel: AboutViewModel by viewModel()
    private var _binding: FragmentAboutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupBinding(inflater)

        return binding.root
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