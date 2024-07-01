package pl.skaucieuropy.rozliczwyjazd.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import pl.skaucieuropy.rozliczwyjazd.models.database.DatabaseCamp

@Dao
interface CampDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(camp: DatabaseCamp): Long

    @Update
    fun update(camp: DatabaseCamp): Int

    @Query("SELECT * FROM camp_table WHERE id = :id")
    fun getCamp(id: Long): DatabaseCamp

    @Query("SELECT * FROM camp_table WHERE isActive = 1")
    fun getActiveCamp(): DatabaseCamp?

    @Query("SELECT id FROM camp_table WHERE isActive = 1")
    fun getActiveCampId(): Long

    @Query("SELECT * FROM camp_table ORDER BY startDate DESC")
    fun getAllCamps(): LiveData<List<DatabaseCamp>>

    @Delete
    fun delete(camp: DatabaseCamp): Int

    @Query("UPDATE camp_table SET isActive = 0 WHERE isActive = 1")
    fun resetIsActive(): Int

    @Query("UPDATE camp_table SET isActive = 1 WHERE id = :campId")
    fun setCampAsActive(campId: Long): Int

    @Query("SELECT COUNT(id) FROM camp_table")
    fun getCampsCount(): Long

    @Query("UPDATE camp_table SET isActive = 1 WHERE id = (SELECT id FROM camp_table ORDER BY startDate DESC LIMIT 1)")
    fun setFirstCampActive(): Int
}