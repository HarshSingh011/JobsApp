package com.example.main_project.candidate.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.candidate.DataClasses.ExperienceShowDataClasses
import com.example.main_project.databinding.ExperienceEditBinding

class ExperienceEditAdapter : ListAdapter<ExperienceShowDataClasses, ExperienceEditAdapter.ExperienceViewHolder>(ExperienceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceViewHolder {
        val binding = ExperienceEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExperienceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExperienceViewHolder, position: Int) {
        val experience = getItem(position)
        holder.bind(experience)
    }

    class ExperienceViewHolder(private val binding: ExperienceEditBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(experience: ExperienceShowDataClasses) {
            binding.CompanyEdit.setText(experience.CompanyName)
            binding.yearOfWorkEdit.setText(experience.YearOfWork)
            binding.positionEdit.setText(experience.Date)
            println("Binding experience item: $experience")
        }
    }

    private class ExperienceDiffCallback : DiffUtil.ItemCallback<ExperienceShowDataClasses>() {
        override fun areItemsTheSame(oldItem: ExperienceShowDataClasses, newItem: ExperienceShowDataClasses): Boolean {
            return oldItem.CompanyName == newItem.CompanyName && oldItem.Date == newItem.Date
        }

        override fun areContentsTheSame(oldItem: ExperienceShowDataClasses, newItem: ExperienceShowDataClasses): Boolean {
            return oldItem == newItem
        }
    }
}

