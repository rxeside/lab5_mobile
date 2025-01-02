package com.example.todo.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.Model.DiaryRecord
import com.example.todo.databinding.ItemDiaryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class DiaryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

	private val binding = ItemDiaryBinding.bind(view)

	fun bindData(
		diaryRecord: DiaryRecord,
		navigateToEditor: (Bundle) -> Unit,
		removeDiaryRecord: (DiaryRecord) -> Unit
	) {
		binding.recordTitle.text = diaryRecord.title
		binding.recordContent.text = diaryRecord.content

		val formattedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(diaryRecord.createdAt)
		binding.recordDate.text = formattedDate

		binding.root.setOnClickListener {
			val arguments = Bundle().apply {
				putString("TITLE", diaryRecord.title)
				putString("CONTENT", diaryRecord.content)
				putString("ID", diaryRecord.uid)
				putLong("DATE", diaryRecord.createdAt)
			}
			navigateToEditor(arguments)
		}

		binding.recordDeleteButton.setOnClickListener {
			removeDiaryRecord(diaryRecord)
		}
	}
}

class DiaryAdapter(
	private val gotoEditorFn: (arguments: Bundle) -> Unit,
	private val deleteDiaryRecord: (diaryRecord: DiaryRecord) -> Unit
) : RecyclerView.Adapter<DiaryViewHolder>() {

	var diaryRecordList: List<DiaryRecord> = emptyList()

	override fun getItemCount(): Int = diaryRecordList.size

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		val binding = ItemDiaryBinding.inflate(inflater, parent, false)
		return DiaryViewHolder(binding.root)
	}

	override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
		val diaryRecord = diaryRecordList[position]
		holder.bindData(diaryRecord, gotoEditorFn, deleteDiaryRecord)
	}
}
