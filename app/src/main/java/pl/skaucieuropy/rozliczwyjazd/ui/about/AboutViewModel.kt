package pl.skaucieuropy.rozliczwyjazd.ui.about

import androidx.lifecycle.ViewModel
import pl.skaucieuropy.rozliczwyjazd.BuildConfig

class AboutViewModel : ViewModel() {
    val versionName = BuildConfig.VERSION_NAME
}