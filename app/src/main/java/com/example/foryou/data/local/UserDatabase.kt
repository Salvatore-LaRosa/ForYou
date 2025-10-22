package com.example.foryou.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.foryou.data.local.dao.MedicineDao
import com.example.foryou.data.local.dao.UserDao
import com.example.foryou.data.local.entity.Medicine
import com.example.foryou.data.local.entity.MedicineIntake
import com.example.foryou.data.local.entity.User

/**
 * The main database for the application.
 * This class is annotated with [@Database] and defines the entities, version, and DAOs.
 *
 * @property userDao Provides access to the user data.
 * @property medicineDao Provides access to the medicine and intake data.
 */
@Database(
    entities = [User::class, Medicine::class, MedicineIntake::class],
    version = 2, // The version of the database schema. Must be incremented on schema changes.
    exportSchema = false // Schema export is disabled. For production apps, it's a good practice to version your schema.
)
abstract class UserDatabase : RoomDatabase() {

    /**
     * Abstract method to get the Data Access Object for the User entity.
     * @return The [UserDao] instance.
     */
    abstract fun userDao(): UserDao

    /**
     * Abstract method to get the Data Access Object for the Medicine and MedicineIntake entities.
     * @return The [MedicineDao] instance.
     */
    abstract fun medicineDao(): MedicineDao

    companion object {
        // @Volatile ensures that the value of INSTANCE is always up-to-date and the same for all execution threads.
        @Volatile
        private var INSTANCE: UserDatabase? = null

        /**
         * Gets the singleton instance of the [UserDatabase].
         * This method uses a synchronized block to prevent race conditions during instance creation.
         *
         * @param context The application context.
         * @return The singleton [UserDatabase] instance.
         */
        fun getDatabase(context: Context): UserDatabase {
            // Return the existing instance if it's not null.
            return INSTANCE ?: synchronized(this) {
                // If the instance is null, create it inside a synchronized block.
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "for_you_database" // The name of the database file.
                )
                    // Disallows destructive migrations. If a migration is not found, an exception will be thrown.
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                // Return the newly created instance.
                instance
            }
        }
    }
}
