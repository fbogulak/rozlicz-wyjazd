package pl.skaucieuropy.rozliczwyjazd.database

import androidx.room.*
import pl.skaucieuropy.rozliczwyjazd.constants.GROCERIES
import pl.skaucieuropy.rozliczwyjazd.models.database.DatabaseDocument

@Dao
interface DocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(document: DatabaseDocument): Long

    @Update
    fun update(document: DatabaseDocument): Int

    @Query("SELECT * FROM document_table WHERE id = :id")
    fun getDocument(id: Long): DatabaseDocument

    @Query(
        "SELECT d.* FROM document_table d INNER JOIN camp_table c ON d.campId = c.id WHERE c.isActive = 1 " +
                "ORDER BY CASE :orderBy " +
                "WHEN 'DATE' THEN date " +
                "WHEN 'CREATION_TIMESTAMP' THEN creationTimestamp END DESC"
    )
    fun getActiveDocuments(orderBy: String): List<DatabaseDocument>

    @Query("SELECT * FROM document_table WHERE campId = :campId ORDER BY date ASC")
    fun getDocumentsByCampId(campId: Long): List<DatabaseDocument>

    @Delete
    fun delete(document: DatabaseDocument): Int

    @Query("DELETE FROM document_table WHERE campId = :campId")
    fun deleteDocumentsByCampId(campId: Long): Int

    @Query(
        "SELECT d.* FROM document_table d INNER JOIN camp_table c ON d.campId = c.id WHERE c.isActive = 1 AND category || ' ' || type || ' ' || number || ' ' || amount || ' ' || comment LIKE :expression " +
                "ORDER BY CASE :orderBy " +
                "WHEN 'DATE' THEN date " +
                "WHEN 'CREATION_TIMESTAMP' THEN creationTimestamp END DESC"
    )
    fun getFilteredDocuments(expression: String, orderBy: String): List<DatabaseDocument>

    @Query("SELECT SUM(amount) FROM document_table WHERE campId = (SELECT id FROM camp_table WHERE isActive = 1)")
    fun getActiveCampExpenses(): Double

    @Query("SELECT SUM(amount) FROM document_table d INNER JOIN camp_table c ON d.campId = c.id WHERE c.isActive = 1 AND d.category = '$GROCERIES'")
    fun getActiveCampGroceriesExpenses(): Double
}