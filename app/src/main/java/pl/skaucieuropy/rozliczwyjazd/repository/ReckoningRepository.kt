package pl.skaucieuropy.rozliczwyjazd.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.database.ReckoningDatabase
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import pl.skaucieuropy.rozliczwyjazd.models.Document

class ReckoningRepository(private val database: ReckoningDatabase) {

    val allCamps by lazy { database.campDao.getAllCamps() }
    val activeDocuments by lazy { database.documentDao.getActiveDocuments() }

    suspend fun getDocumentById(id: Long): Document = withContext(Dispatchers.IO) {
        return@withContext database.documentDao.getDocument(id)
    }

    suspend fun insertDocument(document: Document): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val newId = database.documentDao.insert(document)
            if (newId > 0) {
                return@withContext Result.success(R.string.document_saved)
            } else
                return@withContext Result.failure(Throwable("Błąd - dokument nie zapisany"))
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    suspend fun updateDocument(document: Document): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val rowsUpdated = database.documentDao.update(document)
            if (rowsUpdated > 0) {
                return@withContext Result.success(R.string.document_saved)
            } else
                return@withContext Result.failure(Throwable("Błąd - dokument nie zapisany"))
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    suspend fun getActiveCampId(): Long = withContext(Dispatchers.IO) {
        return@withContext database.campDao.getActiveCampId()
    }

    suspend fun deleteDocument(document: Document): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val rowsDeleted = database.documentDao.delete(document)
            if (rowsDeleted > 0) {
                return@withContext Result.success(R.string.document_deleted)
            } else
                return@withContext Result.failure(Throwable("Błąd - dokument nie usunięty"))
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    suspend fun getCampById(id: Long): Camp = withContext(Dispatchers.IO) {
        return@withContext database.campDao.getCamp(id)
    }

    suspend fun insertCamp(camp: Camp): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val newId = database.campDao.insert(camp)
            if (newId <= 0) {
                return@withContext Result.failure(Throwable("Błąd - obóz nie zapisany"))
            }
            var rowsUpdated = database.campDao.resetIsActive()
            if (rowsUpdated <= 0) {
                return@withContext Result.failure(Throwable("Błąd - obóz nie wybrany jako aktywny"))
            }
            rowsUpdated = database.campDao.setCampAsActive(newId)
            if (rowsUpdated <= 0) {
                return@withContext Result.failure(Throwable("Błąd - żaden obóz nie wybrany jako aktywny"))
            }
            return@withContext Result.success(R.string.camp_saved)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    suspend fun updateCamp(camp: Camp): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val rowsUpdated = database.campDao.update(camp)
            if (rowsUpdated > 0) {
                return@withContext Result.success(R.string.camp_saved)
            } else
                return@withContext Result.failure(Throwable("Błąd - obóz nie zapisany"))
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    suspend fun deleteCamp(camp: Camp): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val campId = camp.id.value
            if (campId == null || campId <= 0) {
                return@withContext Result.failure(Throwable("Błąd - obóz nie usunięty"))
            }
            database.documentDao.deleteDocumentsByCampId(campId)
            if (database.campDao.delete(camp) <= 0) {
                return@withContext Result.failure(Throwable("Błąd - obóz nie usunięty"))
            }
            val numberOfCamps = database.campDao.getCampsCount()
            if (numberOfCamps == 0L) {
                database.campDao.insert(Camp.default())
            }
            if (camp.isActive.value == true) {
                if (database.campDao.setFirstCampActive() <= 0) {
                    return@withContext Result.failure(Throwable("Błąd - żaden obóz nie wybrany jako aktywny"))
                }
            }
            return@withContext Result.success(R.string.camp_deleted)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    suspend fun changeActiveCamp(campId: Long) = withContext(Dispatchers.IO) {
        database.campDao.resetIsActive()
        database.campDao.setCampAsActive(campId)
    }
}