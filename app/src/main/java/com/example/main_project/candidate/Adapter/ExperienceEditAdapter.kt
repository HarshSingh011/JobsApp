package com.example.main_project.candidate.Adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.candidate.DataClasses.ExperienceShowDataClasses
import com.example.main_project.databinding.ExperienceEditBinding

class ExperienceEditAdapter : ListAdapter<ExperienceShowDataClasses, ExperienceEditAdapter.ExperienceViewHolder>(ExperienceDiffCallback()) {

    private val itemList = mutableListOf<ExperienceShowDataClasses>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceViewHolder {
        val binding = ExperienceEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExperienceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExperienceViewHolder, position: Int) {
        val experience = getItem(position)
        holder.bind(experience)
    }

    override fun submitList(list: List<ExperienceShowDataClasses>?) {
        super.submitList(list)
        if (list != null) {
            itemList.clear()
            itemList.addAll(list)
        }
    }

    fun getUpdatedList(): List<ExperienceShowDataClasses> {
        return itemList
    }

    inner class ExperienceViewHolder(private val binding: ExperienceEditBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(experience: ExperienceShowDataClasses) {
            binding.CompanyEdit.setText(experience.CompanyName)
            binding.yearOfWorkEdit.setText(experience.YearOfWork)
            binding.positionEdit.setText(experience.Date)

            binding.CompanyEdit.addTextChangedListener(createTextWatcher { experience.CompanyName = it })
            binding.yearOfWorkEdit.addTextChangedListener(createTextWatcher { experience.YearOfWork = it })
            binding.positionEdit.addTextChangedListener(createTextWatcher { experience.Date = it })
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

    private class ExperienceDiffCallback : DiffUtil.ItemCallback<ExperienceShowDataClasses>() {
        override fun areItemsTheSame(oldItem: ExperienceShowDataClasses, newItem: ExperienceShowDataClasses): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ExperienceShowDataClasses, newItem: ExperienceShowDataClasses): Boolean {
            return oldItem == newItem
        }
    }
}

