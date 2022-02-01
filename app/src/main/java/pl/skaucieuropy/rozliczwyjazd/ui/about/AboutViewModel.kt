package pl.skaucieuropy.rozliczwyjazd.ui.about

import pl.skaucieuropy.rozliczwyjazd.BuildConfig
import pl.skaucieuropy.rozliczwyjazd.ui.base.BaseViewModel

class AboutViewModel : BaseViewModel() {
    val versionName = BuildConfig.VERSION_NAME
}