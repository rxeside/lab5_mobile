package com.example.todo.Infrastructure

import android.app.Application
import androidx.room.Room
import com.example.todo.Model.AppDatabase

class StorageApp : Application() {
	companion object {
		lateinit var db: AppDatabase
	}

	override fun onCreate() {
		super.onCreate()

		db = Room.databaseBuilder(
			applicationContext,
			AppDatabase::class.java,
			"diary-database",
		)
			.addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
			.build()
	}
}