package pl.skaucieuropy.rozliczwyjazd

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class ReckoningApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}