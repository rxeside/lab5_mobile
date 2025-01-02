package com.example.todo.UI

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.todo.ViewModel.DiaryViewModel
import com.example.todo.Model.DiaryRecord
import com.example.todo.R
import com.example.todo.databinding.FragmentDiaryBinding
import kotlinx.coroutines.launch
import java.util.*

class FragmentDiary : Fragment(R.layout.fragment_diary) {
	private val viewModel: DiaryViewModel by activityViewModels()
	private lateinit var binding: FragmentDiaryBinding
	private lateinit var diaryAdapter: DiaryAdapter

	@SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding = FragmentDiaryBinding.bind(view)
		diaryAdapter = DiaryAdapter(
			deleteDiaryRecord = { diaryRecord: DiaryRecord ->
				viewModel.deleteDiaryRecord(diaryRecord)
			},
			gotoEditorFn = { args: Bundle ->
				findNavController().navigate(R.id.action_fragmentDiary_to_fragmentEditor, args)
			}
		)
		binding.listView.adapter = diaryAdapter

		binding.createButton.setOnClickListener {
			findNavController().navigate(R.id.action_fragmentDiary_to_fragmentEditor)
		}

		binding.viewCreateButton.setOnClickListener {
			findNavController().navigate(R.id.action_fragmentDiary_to_fragmentEditor)
		}

		lifecycleScope.launch {
			viewModel.state.collect { diaryRecords ->
				diaryAdapter.diaryRecordList = diaryRecords.filteredRecords
				diaryAdapter.notifyDataSetChanged()
				updateVisibility(diaryRecords.filteredRecords.isEmpty())

				binding.filterButton.setOnClickListener {
					if (diaryRecords.isFiltered) {
						resetFilter()
					} else {
						showDatePicker()
					}
				}
			}
		}
	}

	private fun updateVisibility(isEmpty: Boolean) {
		binding.viewDiary.visibility = View.GONE
		binding.emptyDiary.visibility = View.GONE
		if (isEmpty) {
			binding.emptyDiary.visibility = View.VISIBLE
			binding.viewDiary.visibility = View.GONE
		} else {
			binding.emptyDiary.visibility = View.GONE
			binding.viewDiary.visibility = View.VISIBLE
		}
	}

	private fun showDatePicker() {
		val calendar = Calendar.getInstance()
		val year = calendar.get(Calendar.YEAR)
		val month = calendar.get(Calendar.MONTH)
		val day = calendar.get(Calendar.DAY_OF_MONTH)

		DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
			val selectedDate = Calendar.getInstance().apply {
				set(selectedYear, selectedMonth, selectedDay)
			}
			viewModel.filterRecordsByDate(selectedDate.time)
		}, year, month, day).show()
	}

	private fun resetFilter() {
		viewModel.filterRecordsByDate(null)
	}
}
