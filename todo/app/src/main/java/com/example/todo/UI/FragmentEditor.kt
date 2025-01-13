package com.example.todo.UI

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.todo.ViewModel.DiaryViewModel
import com.example.todo.R
import com.example.todo.databinding.FragmentCreateRecordBinding
import java.util.Locale

class FragmentEditor : Fragment(R.layout.fragment_create_record) {
	private val viewModel: DiaryViewModel by activityViewModels()
	private lateinit var binding: FragmentCreateRecordBinding

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding = FragmentCreateRecordBinding.bind(view)

		val statusOptions = resources.getStringArray(R.array.status_options)
		val spinnerAdapter = ArrayAdapter(
			requireContext(),
			android.R.layout.simple_spinner_item,
			statusOptions
		).apply {
			setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		}
		binding.statusSpinner.adapter = spinnerAdapter


		binding.backButton.setOnClickListener {
			findNavController().navigate(R.id.action_fragmentEditor_to_fragmentDiary)
		}

		// Инициализация поля dueDate
		if (arguments == null) {
			binding.createButton.setOnClickListener { onCreateButtonClick() }
		} else {
			binding.recordTitle.setText(requireArguments().getCharSequence("TITLE"))
			binding.recordContent.setText(requireArguments().getCharSequence("CONTENT"))
			binding.dueDateInput.setText(
				SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
					requireArguments().getLong("DUE_DATE", System.currentTimeMillis())
				)
			)

			val currentStatus = requireArguments().getString("STATUS", "СОЗДАНА")
			binding.statusSpinner.setSelection(statusOptions.indexOf(currentStatus))


			binding.createButton.setOnClickListener {
				updateDiaryRecord(
					uid = requireArguments().getString("ID") as String,
					createdAt = requireArguments().getLong("DATE"),
					dueDate = parseDueDate(binding.dueDateInput.text.toString()),
					status = binding.statusSpinner.selectedItem.toString()
				)
			}
		}
	}


	private fun updateDiaryRecord(uid: String, createdAt: Long, dueDate: Long?, status: String) {
		if (binding.recordTitle.text.isEmpty()) {
			return
		}

		viewModel.updateDiaryRecord(
			uid = uid,
			title = binding.recordTitle.text.toString(),
			content = binding.recordContent.text.toString(),
			createdAt,
			dueDate = dueDate,
			status = binding.statusSpinner.selectedItem.toString()

		)

		findNavController().navigate(R.id.action_fragmentEditor_to_fragmentDiary)
	}

	private fun parseDueDate(dueDateText: String): Long? {
		return try {
			val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
			formatter.parse(dueDateText)?.time
		} catch (e: Exception) {
			null
		}
	}

	private fun onCreateButtonClick() {
		if (binding.recordTitle.text.isEmpty()) return

		viewModel.createDiaryRecord(
			title = binding.recordTitle.text.toString(),
			content = binding.recordContent.text.toString(),
			dueDate = parseDueDate(binding.dueDateInput.text.toString()),
		)

		findNavController().navigate(R.id.action_fragmentEditor_to_fragmentDiary)
	}
}