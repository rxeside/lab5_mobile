package com.example.todo.Model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
	entities = [
		DiaryRecord::class,
	],
	version = 1,
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun diaryRecordDao(): DiaryRecordDao
}