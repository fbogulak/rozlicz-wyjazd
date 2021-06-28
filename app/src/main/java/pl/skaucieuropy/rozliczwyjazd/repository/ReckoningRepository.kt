package pl.skaucieuropy.rozliczwyjazd.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.skaucieuropy.rozliczwyjazd.database.ReckoningDatabase
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import pl.skaucieuropy.rozliczwyjazd.models.Document

class ReckoningRepository(private val database: ReckoningDatabase) {

    val allCamps by lazy { database.campDao.getAllCamps() }
    val activeDocuments by lazy { database.documentDao.getActiveDocuments() }

    suspend fun getDocumentById(id: Long): Document = withContext(Dispatchers.IO) {
        return@withContext database.documentDao.getDocument(id)
    }

    suspend fun insertDocument(document: Document) = withContext(Dispatchers.IO) {
        database.documentDao.insert(document)
    }

    suspend fun updateDocument(document: Document) = withContext(Dispatchers.IO) {
        database.documentDao.update(document)
    }

    suspend fun getActiveCampId(): Long = withContext(Dispatchers.IO) {
        return@withContext database.campDao.getActiveCampId()
    }

    suspend fun deleteDocument(document: Document) = withContext(Dispatchers.IO) {
        database.documentDao.delete(document)
    }

    suspend fun getCampById(id: Long): Camp = withContext(Dispatchers.IO) {
        return@withContext database.campDao.getCamp(id)
    }

    suspend fun insertCamp(camp: Camp) = withContext(Dispatchers.IO) {
        database.campDao.insert(camp)
    }

    suspend fun updateCamp(camp: Camp) = withContext(Dispatchers.IO) {
        database.campDao.update(camp)
    }

    suspend fun deleteCamp(camp: Camp) = withContext(Dispatchers.IO) {
        database.campDao.delete(camp)
    }

    suspend fun changeActiveCamp(campId: Long) = withContext(Dispatchers.IO){
        database.campDao.resetIsActive()
        database.campDao.setCampAsActive(campId)
    }
}