package pl.skaucieuropy.rozliczwyjazd.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import pl.skaucieuropy.rozliczwyjazd.database.ReckoningDatabase
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import pl.skaucieuropy.rozliczwyjazd.models.Document

class ReckoningRepository(private val database: ReckoningDatabase) {

    val activeCampId = database.campDao.getActiveCampId()
    val allCamps by lazy { database.campDao.getAllCamps() }
    val activeDocuments = Transformations.map(activeCampId) {
        database.documentDao.getDocumentsByCamp(it)
    }
}