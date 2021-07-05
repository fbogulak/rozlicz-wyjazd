package pl.skaucieuropy.rozliczwyjazd.database

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import pl.skaucieuropy.rozliczwyjazd.models.Document

@Dao
interface CampDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(camp: Camp): Long

    @Update
    fun update(camp: Camp): Int

    @Query("SELECT * FROM camp_table WHERE id = :id")
    fun getCamp(id: Long): Camp

    @Query("SELECT * FROM camp_table WHERE isActive = 1")
    fun getActiveCamp(): LiveData<Camp>

    @Query("SELECT id FROM camp_table WHERE isActive = 1")
    fun getActiveCampId(): Long

    @Query("SELECT * FROM camp_table ORDER BY startDate DESC")
    fun getAllCamps(): LiveData<List<Camp>>

    @Delete
    fun delete(camp: Camp): Int

    @Query("UPDATE camp_table SET isActive = 0 WHERE isActive = 1")
    fun resetIsActive(): Int

    @Query("UPDATE camp_table SET isActive = 1 WHERE id = :campId")
    fun setCampAsActive(campId: Long): Int

    @Query("SELECT COUNT(id) FROM camp_table")
    fun getCampsCount(): Long

    @Query("UPDATE camp_table SET isActive = 1 WHERE id = (SELECT id FROM camp_table ORDER BY startDate DESC LIMIT 1)")
    fun setFirstCampActive(): Int

    @Query("SELECT SUM(amount) FROM document_table WHERE campId = (SELECT id FROM camp_table WHERE isActive = 1)")
    fun getActiveCampExpenses(): LiveData<Double>
}