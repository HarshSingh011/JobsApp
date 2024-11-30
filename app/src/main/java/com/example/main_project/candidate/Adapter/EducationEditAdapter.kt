package com.example.main_project.candidate.Adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.candidate.DataClasses.EducationShowDataClasses
import com.example.main_project.databinding.EducationEditBinding

class EducationEditAdapter : ListAdapter<EducationShowDataClasses, EducationEditAdapter.EducationViewHolder>(EducationDiffCallback()) {

    private val itemList = mutableListOf<EducationShowDataClasses>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EducationViewHolder {
        val binding = EducationEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EducationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EducationViewHolder, position: Int) {
        val education = getItem(position)
        holder.bind(education)
    }

    override fun submitList(list: List<EducationShowDataClasses>?) {
        super.submitList(list)
        if (list != null) {
            itemList.clear()
            itemList.addAll(list)
        }
    }

    fun getUpdatedList(): List<EducationShowDataClasses> {
        return itemList
    }

    inner class EducationViewHolder(private val binding: EducationEditBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(education: EducationShowDataClasses) {
            binding.instituteEdit.setText(education.IntituteName)
            binding.DegreeEdit.setText(education.Degree)
            binding.yearOfCompleletionEdit.setText(education.DateOfCompletion?.toString() ?: "")

            binding.instituteEdit.addTextChangedListener(createTextWatcher { education.IntituteName = it })
            binding.DegreeEdit.addTextChangedListener(createTextWatcher { education.Degree = it })
            binding.yearOfCompleletionEdit.addTextChangedListener(createTextWatcher { education.DateOfCompletion = it.toIntOrNull() })
        }

        private fun createTextWatcher(onTextChanged: (String) -> Unit): TextWatcher {
            return object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    onTextChanged(s.toString())
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        itemList[position] = getItem(position)
                    }
                }
            }
        }
    }

    private class EducationDiffCallback : DiffUtil.ItemCallback<EducationShowDataClasses>() {
        override fun areItemsTheSame(oldItem: EducationShowDataClasses, newItem: EducationShowDataClasses): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: EducationShowDataClasses, newItem: EducationShowDataClasses): Boolean {
            return oldItem == newItem
        }
    }
}

