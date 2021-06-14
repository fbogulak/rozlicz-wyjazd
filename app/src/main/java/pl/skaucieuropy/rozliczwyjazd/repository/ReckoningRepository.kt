package pl.skaucieuropy.rozliczwyjazd.repository

import androidx.lifecycle.LiveData
import pl.skaucieuropy.rozliczwyjazd.database.ReckoningDatabase
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import pl.skaucieuropy.rozliczwyjazd.models.Document
import java.util.*

class ReckoningRepository(private val database: ReckoningDatabase) {

    private val activeCamp = database.campDao.getActiveCamp()
    val allCamps: LiveData<List<Camp>> by lazy { database.campDao.getAllCamps() }
    val activeDocuments: LiveData<List<Document>> by lazy {
        database.documentDao.getDocumentsByCamp(activeCamp.value?.id ?: -1)
    }
}