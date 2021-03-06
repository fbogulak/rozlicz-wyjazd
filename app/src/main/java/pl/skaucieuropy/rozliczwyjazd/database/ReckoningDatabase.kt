package pl.skaucieuropy.rozliczwyjazd.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import pl.skaucieuropy.rozliczwyjazd.models.Camp
import pl.skaucieuropy.rozliczwyjazd.models.Document
import java.util.concurrent.Executors

@Database(entities = [Document::class, Camp::class], version = 1)
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
                        .addCallback(object : RoomDatabase.Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                Executors.newSingleThreadExecutor().execute {
                                    instance?.campDao?.insert(Camp.default())
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