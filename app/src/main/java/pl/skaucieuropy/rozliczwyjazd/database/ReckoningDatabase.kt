package pl.skaucieuropy.rozliczwyjazd.database

import android.content.Context
import androidx.room.*
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import pl.skaucieuropy.rozliczwyjazd.models.Document

@Database(entities = [Document::class, Camp::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ReckoningDatabase : RoomDatabase() {

    abstract val documentDao: DocumentDao
    abstract val campDao: CampDao

    companion object {

        @Volatile
        private var INSTANCE: ReckoningDatabase? = null

        fun getInstance(context: Context): ReckoningDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ReckoningDatabase::class.java,
                        "reckoning_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}