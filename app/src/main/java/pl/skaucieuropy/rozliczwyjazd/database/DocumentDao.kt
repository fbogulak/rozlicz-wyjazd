package pl.skaucieuropy.rozliczwyjazd.database

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.skaucieuropy.rozliczwyjazd.models.Document

@Dao
interface DocumentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDocument(document: Document)

    @Query("SELECT * FROM document_table WHERE id = :id")
    fun getDocument(id: Long): Document

    @Query("SELECT * FROM document_table WHERE campId = :campId ORDER BY date DESC")
    fun getDocumentsByCamp(campId: Long): LiveData<List<Document>>

    @Delete
    fun delete(document: Document)
}