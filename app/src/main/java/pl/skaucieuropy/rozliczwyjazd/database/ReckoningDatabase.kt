package pl.skaucieuropy.rozliczwyjazd.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import pl.skaucieuropy.rozliczwyjazd.models.Document
import java.util.*
import java.util.concurrent.Executors

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
                        .addCallback(object : RoomDatabase.Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                Executors.newSingleThreadExecutor().execute {
                                    instance?.let{
                                        it.campDao.insertCamp(Camp(1, "Marydół 2021", 2000.0, Date(), Date(), true))
                                        it.documentDao.insertDocument(Document(1, "1981/11/2019", "Faktura", Date(), "Art. spożywcze", 24.54, "", false, 1))
                                        it.documentDao.insertDocument(Document(2, "1783F01107/12/19", "Faktura", Date(), "Art. przemysłowe", 382.99, "", false, 1))
                                    }
                                }
                            }
                        })
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}