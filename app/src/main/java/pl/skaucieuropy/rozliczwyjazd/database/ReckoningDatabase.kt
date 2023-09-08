package pl.skaucieuropy.rozliczwyjazd.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import pl.skaucieuropy.rozliczwyjazd.models.database.DatabaseCamp
import pl.skaucieuropy.rozliczwyjazd.models.database.DatabaseDocument
import pl.skaucieuropy.rozliczwyjazd.models.domain.Camp
import pl.skaucieuropy.rozliczwyjazd.models.domain.asDatabaseModel
import java.util.concurrent.Executors

@Database(
    entities = [DatabaseDocument::class, DatabaseCamp::class],
    version = 3,
    autoMigrations = [AutoMigration(
        from = 2,
        to = 3,
        spec = ReckoningDatabase.TwoToThreeAutoMigration::class
    )]
)
abstract class ReckoningDatabase : RoomDatabase() {
    @DeleteColumn(
        tableName = "document_table",
        columnName = "isFromTravelVoucher"
    )
    class TwoToThreeAutoMigration : AutoMigrationSpec

    abstract val documentDao: DocumentDao
    abstract val campDao: CampDao

    companion object {

        @Volatile
        private var INSTANCE: ReckoningDatabase? = null

        fun getInstance(context: Context): ReckoningDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {

                    val MIGRATION_1_2 = object : Migration(1, 2) {
                        override fun migrate(database: SupportSQLiteDatabase) {
                            database.execSQL("ALTER TABLE `document_table` ADD COLUMN `creationTimestamp` INTEGER NOT NULL DEFAULT 0")
                            database.execSQL("UPDATE `document_table` SET `creationTimestamp` = `date`")
                        }
                    }

                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ReckoningDatabase::class.java,
                        "reckoning_database"
                    )
                        .addCallback(object : RoomDatabase.Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                Executors.newSingleThreadExecutor().execute {
                                    instance?.campDao?.insert(Camp.default().asDatabaseModel())
                                }
                            }
                        })
                        .addMigrations(MIGRATION_1_2)
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}