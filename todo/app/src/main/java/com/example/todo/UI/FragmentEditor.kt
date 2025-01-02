package com.example.todo.UI

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.todo.ViewModel.DiaryViewModel
import com.example.todo.R
import com.example.todo.databinding.FragmentCreateRecordBinding

class FragmentEditor : Fragment(R.layout.fragment_create_record) {
	private val viewModel: DiaryViewModel by activityViewModels()
	private lateinit var binding: FragmentCreateRecordBinding

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding = FragmentCreateRecordBinding.bind(view)

		binding.backButton.setOnClickListener {
			findNavController().navigate(R.id.action_fragmentEditor_to_fragmentDiary)
		}

		if (arguments == null) {
			binding.createButton.setOnClickListener { onCreateButtonClick() }
		} else {
			binding.recordTitle.setText(requireArguments().getCharSequence("TITLE"))
			binding.recordContent.setText(requireArguments().getCharSequence("CONTENT"))
			binding.createButton.setOnClickListener { updateDiaryRecord(
				uid = requireArguments().getString("ID") as String,
				requireArguments().getLong("DATE")
			) }
		}
	}

	private fun updateDiaryRecord(uid: String, createdAt: Long) {
		if (binding.recordTitle.text.isEmpty()) {
			return
		}

		viewModel.updateDiaryRecord(
			uid,
			title = binding.recordTitle.text.toString(),
			content = binding.recordContent.text.toString(),
			createdAt
			)

		findNavController().navigate(R.id.action_fragmentEditor_to_fragmentDiary)
	}

	private fun onCreateButtonClick() {
		if (binding.recordTitle.text.isEmpty()) {
			return
		}

		viewModel.createDiaryRecord(
			binding.recordTitle.text.toString(),
			binding.recordContent.text.toString()
		)

		findNavController().navigate(R.id.action_fragmentEditor_to_fragmentDiary)
	}
}