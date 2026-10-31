package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BookEntity::class,
        BookmarkEntity::class,
        SaleTransactionEntity::class,
        ReviewEntity::class,
        UserProfileEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mana_kitaabaa.db"
                ).fallbackToDestructiveMigration(true)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate with initial books, reviews, and profile
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(context)
                            val dao = database.bookDao()
                            dao.insertBooks(InitialBookData.getSampleBooks())
                            dao.insertReviews(InitialBookData.getInitialReviews())
                            dao.insertUserProfile(InitialBookData.getDefaultProfile())
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
