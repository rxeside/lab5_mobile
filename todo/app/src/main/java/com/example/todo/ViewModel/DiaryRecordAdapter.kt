package com.example.todo.ViewModel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.Model.DiaryRecord
import com.example.todo.R
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
		binding.recordStatus.text = itemView.context.getString(R.string.status_format, diaryRecord.status)


		val formattedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(diaryRecord.createdAt)
		binding.recordDate.text = itemView.context.getString(R.string.created_date_format, formattedDate)

		diaryRecord.dueDate?.let {
			val dueDateFormatted = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(it)
			binding.recordDate.text = itemView.context.getString(R.string.due_date_format, dueDateFormatted)
		} ?: run {
			binding.recordDate.text = "Срок: не указан"
		}

		binding.root.setOnClickListener {
			val bundle = Bundle().apply {
				putString("ID", diaryRecord.uid)
				putCharSequence("TITLE", diaryRecord.title)
				putCharSequence("CONTENT", diaryRecord.content)
				putLong("DATE", diaryRecord.createdAt)
				putLong("DUE_DATE", diaryRecord.dueDate ?: System.currentTimeMillis())
				putString("STATUS", diaryRecord.status)
			}
			navigateToEditor(bundle)
		}

		binding.recordDeleteButton.setOnClickListener { removeDiaryRecord(diaryRecord) }
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
