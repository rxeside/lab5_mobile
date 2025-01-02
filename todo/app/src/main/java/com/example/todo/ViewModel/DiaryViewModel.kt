package com.example.todo.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.Infrastructure.StorageApp
import com.example.todo.Model.DiaryRecord
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class RecordsState(
	val allRecords: List<DiaryRecord>,
	val filteredRecords: List<DiaryRecord>,
	val isFiltered: Boolean
)

class DiaryViewModel : ViewModel() {
	private val _state = MutableStateFlow(RecordsState(emptyList(), emptyList(), false))
	val state: StateFlow<RecordsState> = _state

	private var currentFilterDate: Date? = null

	init {
		viewModelScope.launch {
			StorageApp.db.diaryRecordDao().getAllAsFlow().collect { allRecords ->
				_state.value = _state.value.copy(
					allRecords = allRecords,
					filteredRecords = applyCurrentFilter(allRecords),
					isFiltered = currentFilterDate != null
				)
			}
		}
	}

	fun filterRecordsByDate(date: Date?) {
		currentFilterDate = date
		_state.value = _state.value.copy(
			filteredRecords = applyCurrentFilter(_state.value.allRecords),
			isFiltered = date != null
		)
	}

	private fun applyCurrentFilter(records: List<DiaryRecord>): List<DiaryRecord> {
		val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
		return if (currentFilterDate == null) {
			records
		} else {
			records.filter { diaryRecord ->
				formatter.format(diaryRecord.createdAt) == currentFilterDate?.let {
					formatter.format(
						it
					)
				}
			}
		}
	}

	fun createDiaryRecord(title: String, content: String) {
		viewModelScope.launch {
			val diaryRecordDao = StorageApp.db.diaryRecordDao()
			if (diaryRecordDao.findByTitle(title) != null) {
				return@launch
			}

			val randomUid = UUID.randomUUID().toString()
			val newDiaryRecord = DiaryRecord(
				randomUid,
				title,
				content,
				System.currentTimeMillis(),
			)

			diaryRecordDao.insertAll(newDiaryRecord)
		}
	}

	fun updateDiaryRecord(uid: String, title: String, content: String, date: Long) {
		viewModelScope.launch {
			val diaryRecordDao = StorageApp.db.diaryRecordDao()

			val diaryRecord = DiaryRecord(
				uid,
				title,
				content,
				date,
			)

			diaryRecordDao.updateAll(diaryRecord)
		}
	}

	fun deleteDiaryRecord(diaryRecord: DiaryRecord) {
		viewModelScope.launch {
			val diaryRecordDao = StorageApp.db.diaryRecordDao()
			diaryRecordDao.delete(diaryRecord)
		}
	}
}
