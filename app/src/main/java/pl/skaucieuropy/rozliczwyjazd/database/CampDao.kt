package pl.skaucieuropy.rozliczwyjazd.database

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.skaucieuropy.rozliczwyjazd.models.Camp

@Dao
interface CampDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCamp(camp: Camp)

    @Query("SELECT * FROM camp_table WHERE id = :id")
    fun getCamp(id: Long): Camp

    @Query("SELECT * FROM camp_table WHERE isActive = 1")
    fun getActiveCamp(): LiveData<Camp>

    @Query("SELECT * FROM camp_table ORDER BY startDate DESC")
    fun getAllCamps(): LiveData<List<Camp>>

    @Delete
    fun delete(camp: Camp)
}