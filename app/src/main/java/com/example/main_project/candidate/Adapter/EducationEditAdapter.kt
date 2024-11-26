package com.example.main_project.candidate.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.candidate.DataClasses.EducationShowDataClasses
import com.example.main_project.databinding.EducationEditBinding

class EducationEditAdapter : ListAdapter<EducationShowDataClasses, EducationEditAdapter.EducationViewHolder>(EducationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EducationViewHolder {
        val binding = EducationEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EducationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EducationViewHolder, position: Int) {
        val education = getItem(position)
        holder.bind(education)
    }

    class EducationViewHolder(private val binding: EducationEditBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(education: EducationShowDataClasses) {
            binding.instituteEdit.setText(education.IntituteName)
            binding.DegreeEdit.setText(education.Degree)
            binding.yearOfCompleletionEdit.setText(education.DateOfCompletion?.toString() ?: "N/A")
            println("Binding education item: $education")
        }
    }

    private class EducationDiffCallback : DiffUtil.ItemCallback<EducationShowDataClasses>() {
        override fun areItemsTheSame(oldItem: EducationShowDataClasses, newItem: EducationShowDataClasses): Boolean {
            return oldItem.IntituteName == newItem.IntituteName && oldItem.Degree == newItem.Degree
        }

        override fun areContentsTheSame(oldItem: EducationShowDataClasses, newItem: EducationShowDataClasses): Boolean {
            return oldItem == newItem
        }
    }
}

