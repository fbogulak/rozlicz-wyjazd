package pl.skaucieuropy.rozliczwyjazd.database

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.skaucieuropy.rozliczwyjazd.models.Document

@Dao
interface DocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(document: Document)

    @Update
    fun update(document: Document)

    @Query("SELECT * FROM document_table WHERE id = :id")
    fun getDocument(id: Long): Document

    @Query("SELECT * FROM document_table WHERE campId = (SELECT id FROM camp_table WHERE isActive = 1) ORDER BY date DESC")
    fun getActiveDocuments(): LiveData<List<Document>>

    @Delete
    fun delete(document: Document)
}