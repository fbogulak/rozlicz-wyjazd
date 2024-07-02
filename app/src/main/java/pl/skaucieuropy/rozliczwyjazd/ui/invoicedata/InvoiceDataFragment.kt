package pl.skaucieuropy.rozliczwyjazd.ui.invoicedata

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.databinding.FragmentInvoiceDataBinding

class InvoiceDataFragment : Fragment() {

    private lateinit var binding: FragmentInvoiceDataBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setupBinding(inflater)
        setupListeners()

        return binding.root
    }

    private fun setupBinding(inflater: LayoutInflater) {
        binding = FragmentInvoiceDataBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupListeners() {
        binding.shareButton.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT, getString(
                        R.string.invoice_data_format, getString(R.string.invoice_data),
                        getString(R.string.scouts_of_europe),
                        getString(R.string.address_value),
                        getString(R.string.tax_number)
                    )
                )
                putExtra(Intent.EXTRA_TITLE, getString(R.string.invoice_data))
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }
}