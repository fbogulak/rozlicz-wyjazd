package pl.skaucieuropy.rozliczwyjazd.database

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.skaucieuropy.rozliczwyjazd.models.database.DatabaseDocument

@Dao
interface DocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(document: DatabaseDocument): Long

    @Update
    fun update(document: DatabaseDocument): Int

    @Query("SELECT * FROM document_table WHERE id = :id")
    fun getDocument(id: Long): DatabaseDocument

    @Query("SELECT d.* FROM document_table d INNER JOIN camp_table c ON d.campId = c.id WHERE c.isActive = 1 ORDER BY dateMillis DESC")
    fun getActiveDocuments(): LiveData<List<DatabaseDocument>>

    @Query("SELECT * FROM document_table WHERE campId = :campId ORDER BY dateMillis ASC")
    fun getDocumentsByCampId(campId: Long): List<DatabaseDocument>

    @Delete
    fun delete(document: DatabaseDocument): Int

    @Query("DELETE FROM document_table WHERE campId = :campId")
    fun deleteDocumentsByCampId(campId: Long): Int

    @Query("SELECT d.* FROM document_table d INNER JOIN camp_table c ON d.campId = c.id WHERE c.isActive = 1 AND category || ' ' || type || ' ' || number || ' ' || amount || ' ' || comment LIKE :expression ORDER BY dateMillis DESC")
    fun getFilteredDocuments(expression: String): LiveData<List<DatabaseDocument>>
}