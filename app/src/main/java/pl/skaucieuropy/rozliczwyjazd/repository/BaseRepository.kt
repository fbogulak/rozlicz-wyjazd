package pl.skaucieuropy.rozliczwyjazd.repository

import androidx.lifecycle.LiveData
import pl.skaucieuropy.rozliczwyjazd.models.domain.Camp
import pl.skaucieuropy.rozliczwyjazd.models.domain.Document

interface BaseRepository {
    suspend fun getDocumentById(id: Long): Document
    suspend fun insertDocument(document: Document): Result<Int>
    suspend fun updateDocument(document: Document): Result<Int>
    suspend fun getActiveCampId(): Long
    suspend fun deleteDocument(document: Document): Result<Int>
    suspend fun getDocumentsByCampId(campId: Long): List<Document>
    fun getActiveDocuments(query: String?): LiveData<List<Document>>
    suspend fun getAllCamps(): LiveData<List<Camp>>
    suspend fun getActiveCamp(): Camp
    suspend fun getCampById(id: Long): Camp
    suspend fun insertCamp(camp: Camp): Result<Int>
    suspend fun updateCamp(camp: Camp): Result<Int>
    suspend fun deleteCamp(camp: Camp): Result<Int>
    suspend fun changeActiveCamp(campId: Long): Result<Int>
    suspend fun getActiveCampExpenses(): Double
}