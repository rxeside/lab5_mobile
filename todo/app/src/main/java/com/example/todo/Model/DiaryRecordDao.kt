package com.example.todo.Model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryRecordDao {
	@Query("SELECT * FROM diaryrecord")
	fun getAllAsFlow(): Flow<List<DiaryRecord>>

	@Insert
	suspend fun insertAll(vararg diaryRecord: DiaryRecord)

	@Update
	suspend fun updateAll(vararg diaryRecord: DiaryRecord)

	@Query("SELECT * FROM diaryrecord " +
			"WHERE title LIKE :title LIMIT 1")
	suspend fun findByTitle(title: String): DiaryRecord?

	@Delete
	suspend fun delete(record: DiaryRecord)
}