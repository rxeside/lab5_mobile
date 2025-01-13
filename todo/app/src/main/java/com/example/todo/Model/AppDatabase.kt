package com.example.todo.Model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
	entities = [
		DiaryRecord::class,
	],
	version = 3,
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun diaryRecordDao(): DiaryRecordDao

	companion object {
		val MIGRATION_1_2 = object : Migration(1, 2) {
			override fun migrate(db: SupportSQLiteDatabase) {
				db.execSQL("ALTER TABLE DiaryRecord ADD COLUMN due_date INTEGER")
			}
		}

		val MIGRATION_2_3 = object : Migration(2, 3) {
			override fun migrate(db: SupportSQLiteDatabase) {
				db.execSQL("ALTER TABLE DiaryRecord ADD COLUMN status TEXT NOT NULL DEFAULT 'CREATED'")
			}
		}
	}
}
