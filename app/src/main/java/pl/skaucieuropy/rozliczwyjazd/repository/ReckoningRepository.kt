package pl.skaucieuropy.rozliczwyjazd.repository

import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.skaucieuropy.rozliczwyjazd.R
import pl.skaucieuropy.rozliczwyjazd.database.ReckoningDatabase
import pl.skaucieuropy.rozliczwyjazd.models.database.asDomainModel
import pl.skaucieuropy.rozliczwyjazd.models.domain.Camp
import pl.skaucieuropy.rozliczwyjazd.models.domain.Document
import pl.skaucieuropy.rozliczwyjazd.models.domain.asDatabaseModel
import pl.skaucieuropy.rozliczwyjazd.utils.DocumentsOrderBy

class ReckoningRepository(private val database: ReckoningDatabase) : BaseRepository {

    override val allCamps by lazy { database.campDao.getAllCamps().map { it.asDomainModel() } }

    override suspend fun getDocumentById(id: Long): Document = withContext(Dispatchers.IO) {
        return@withContext database.documentDao.getDocument(id).asDomainModel()
    }

    override suspend fun insertDocument(document: Document): Result<Int> =
        withContext(Dispatchers.IO) {
            try {
                val newId = database.documentDao.insert(document.asDatabaseModel())
                if (newId > 0) {
                    return@withContext Result.success(R.string.document_added)
                } else
                    return@withContext Result.failure(Throwable(ERROR_SAVING_DOCUMENT))
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }

    override suspend fun updateDocument(document: Document): Result<Int> =
        withContext(Dispatchers.IO) {
            try {
                val rowsUpdated = database.documentDao.update(document.asDatabaseModel())
                if (rowsUpdated > 0) {
                    return@withContext Result.success(R.string.changes_saved)
                } else
                    return@withContext Result.failure(Throwable(ERROR_SAVING_DOCUMENT))
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }

    override suspend fun getActiveCampId(): Long = withContext(Dispatchers.IO) {
        return@withContext database.campDao.getActiveCampId()
    }

    override suspend fun deleteDocument(document: Document): Result<Int> =
        withContext(Dispatchers.IO) {
            try {
                val rowsDeleted = database.documentDao.delete(document.asDatabaseModel())
                if (rowsDeleted > 0) {
                    return@withContext Result.success(R.string.document_deleted)
                } else
                    return@withContext Result.failure(Throwable(ERROR_DELETING_DOCUMENT))
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }

    override suspend fun getDocumentsByCampId(campId: Long): List<Document> =
        withContext(Dispatchers.IO) {
            return@withContext database.documentDao.getDocumentsByCampId(campId).asDomainModel()
        }

    override suspend fun getActiveDocuments(query: String?, orderBy: DocumentsOrderBy): List<Document> =
        withContext(Dispatchers.IO) {
            return@withContext if (query.isNullOrEmpty()) {
                database.documentDao.getActiveDocuments(orderBy.name).asDomainModel()
            } else {
                val expression = "%${query.replace(" ", "%")}%"
                database.documentDao.getFilteredDocuments(expression, orderBy.name).asDomainModel()
            }
        }

    override suspend fun getActiveCampExpenses(): Double = withContext(Dispatchers.IO) {
        return@withContext database.documentDao.getActiveCampExpenses()
    }

    override suspend fun getActiveCampGroceriesExpenses(): Double = withContext(Dispatchers.IO) {
        return@withContext database.documentDao.getActiveCampGroceriesExpenses()
    }

    override suspend fun getActiveCamp(): Camp? = withContext(Dispatchers.IO) {
        return@withContext database.campDao.getActiveCamp()?.asDomainModel()
    }

    override suspend fun getCampById(id: Long): Camp = withContext(Dispatchers.IO) {
        return@withContext database.campDao.getCamp(id).asDomainModel()
    }

    override suspend fun insertCamp(camp: Camp): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val newId = database.campDao.insert(camp.asDatabaseModel())
            if (newId <= 0) {
                return@withContext Result.failure(Throwable(ERROR_SAVING_CAMP))
            }
            var rowsUpdated = database.campDao.resetIsActive()
            if (rowsUpdated <= 0) {
                return@withContext Result.failure(Throwable(ERROR_CAMP_NOT_SET_AS_ACTIVE))
            }
            rowsUpdated = database.campDao.setCampAsActive(newId)
            if (rowsUpdated <= 0) {
                return@withContext Result.failure(Throwable(ERROR_NO_CAMP_SET_AS_ACTIVE))
            }
            return@withContext Result.success(R.string.camp_added)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    override suspend fun updateCamp(camp: Camp): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val rowsUpdated = database.campDao.update(camp.asDatabaseModel())
            if (rowsUpdated > 0) {
                return@withContext Result.success(R.string.changes_saved)
            } else
                return@withContext Result.failure(Throwable(ERROR_SAVING_CAMP))
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    override suspend fun deleteCamp(camp: Camp): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val campId = camp.id
            if (campId <= 0) {
                return@withContext Result.failure(Throwable(ERROR_DELETING_CAMP))
            }
            database.documentDao.deleteDocumentsByCampId(campId)
            if (database.campDao.delete(camp.asDatabaseModel()) <= 0) {
                return@withContext Result.failure(Throwable(ERROR_DELETING_CAMP))
            }
            val numberOfCamps = database.campDao.getCampsCount()
            if (numberOfCamps == 0L) {
                database.campDao.insert(Camp.default().asDatabaseModel())
            }
            if (camp.isActive) {
                if (database.campDao.setFirstCampActive() <= 0) {
                    return@withContext Result.failure(Throwable(ERROR_NO_CAMP_SET_AS_ACTIVE))
                }
            }
            return@withContext Result.success(R.string.camp_deleted)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    override suspend fun changeActiveCamp(campId: Long): Result<Int> = withContext(Dispatchers.IO) {
        try {
            var rowsUpdated = database.campDao.resetIsActive()
            if (rowsUpdated <= 0) {
                return@withContext Result.failure(Throwable(ERROR_CAMP_NOT_SET_AS_ACTIVE))
            }
            rowsUpdated = database.campDao.setCampAsActive(campId)
            if (rowsUpdated <= 0) {
                return@withContext Result.failure(Throwable(ERROR_NO_CAMP_SET_AS_ACTIVE))
            }
            return@withContext Result.success(R.string.camp_set_as_active)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    companion object {
        const val ERROR_SAVING_CAMP = "Wystąpił błąd. Nie zapisano obozu."
        const val ERROR_DELETING_CAMP = "Wystąpił błąd. Nie usunięto obozu."
        const val ERROR_SAVING_DOCUMENT = "Wystąpił błąd. Nie zapisano dokumentu."
        const val ERROR_DELETING_DOCUMENT = "Wystąpił błąd. Nie usunięto dokumentu."
        const val ERROR_CAMP_NOT_SET_AS_ACTIVE = "Wystąpił błąd. Nie wybrano obozu jako aktywny."
        const val ERROR_NO_CAMP_SET_AS_ACTIVE =
            "Wystąpił błąd. Nie wybrano żadnego obozu jako aktywny."
    }
}