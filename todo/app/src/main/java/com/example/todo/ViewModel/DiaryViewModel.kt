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

enum class SortType {
	DATE_ASCENDING,
	DATE_DESCENDING,
	ALPHABETICAL
}

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

	fun createDiaryRecord(title: String, content: String, dueDate: Long?, status: String = "СОЗДАНА") {
		viewModelScope.launch {
			val diaryRecordDao = StorageApp.db.diaryRecordDao()
			if (diaryRecordDao.findByTitle(title) != null) return@launch

			val randomUid = UUID.randomUUID().toString()
			val newDiaryRecord = DiaryRecord(
				uid = randomUid,
				title = title,
				content = content,
				createdAt = System.currentTimeMillis(),
				dueDate = dueDate,
				status = status
			)

			diaryRecordDao.insertAll(newDiaryRecord)
		}
	}


	fun updateDiaryRecord(uid: String, title: String, content: String, date: Long, dueDate: Long?, status: String) {
		viewModelScope.launch {
			val diaryRecordDao = StorageApp.db.diaryRecordDao()

			val diaryRecord = DiaryRecord(
				uid = uid,
				title = title,
				content = content,
				createdAt = date,
				dueDate = dueDate,
				status = status
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

	fun sortRecords(sortType: SortType) {
		val sortedRecords = when (sortType) {
			SortType.DATE_ASCENDING -> _state.value.filteredRecords.sortedBy { it.createdAt }
			SortType.DATE_DESCENDING -> _state.value.filteredRecords.sortedByDescending { it.createdAt }
			SortType.ALPHABETICAL -> _state.value.filteredRecords.sortedBy { it.title }
		}
		_state.value = _state.value.copy(filteredRecords = sortedRecords)
	}

	fun searchRecords(query: String) {
		_state.value = _state.value.copy(
			filteredRecords = _state.value.allRecords.filter {
				it.title.contains(query, ignoreCase = true) ||
						it.content.contains(query, ignoreCase = true)
			}
		)
	}

	fun resetSearch() {
		_state.value = _state.value.copy(filteredRecords = _state.value.allRecords)
	}

	fun filterRecordsByStatus(status: String?) {
		_state.value = _state.value.copy(
			filteredRecords = if (status == null) {
				_state.value.allRecords
			} else {
				_state.value.allRecords.filter { it.status == status }
			},
			isFiltered = status != null
		)
	}
}
