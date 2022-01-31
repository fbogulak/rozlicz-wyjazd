package pl.skaucieuropy.rozliczwyjazd

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import pl.skaucieuropy.rozliczwyjazd.database.ReckoningDatabase
import pl.skaucieuropy.rozliczwyjazd.repository.ReckoningRepository
import pl.skaucieuropy.rozliczwyjazd.ui.about.AboutViewModel
import pl.skaucieuropy.rozliczwyjazd.ui.campedit.CampEditViewModel
import pl.skaucieuropy.rozliczwyjazd.ui.camps.CampsViewModel
import pl.skaucieuropy.rozliczwyjazd.ui.documentedit.DocumentEditViewModel
import pl.skaucieuropy.rozliczwyjazd.ui.documents.DocumentsViewModel
import pl.skaucieuropy.rozliczwyjazd.ui.summary.SummaryViewModel

class ReckoningApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        val appModule = module {
            single { ReckoningDatabase.getInstance(this@ReckoningApplication) }
            single { ReckoningRepository(get()) }
            viewModel { AboutViewModel() }
            viewModel { CampEditViewModel(get()) }
            viewModel { CampsViewModel(get()) }
            viewModel { DocumentEditViewModel(get()) }
            viewModel { DocumentsViewModel(get()) }
            viewModel { SummaryViewModel(get()) }
        }

        startKoin {
            androidContext(this@ReckoningApplication)
            modules(appModule)
        }
    }
}