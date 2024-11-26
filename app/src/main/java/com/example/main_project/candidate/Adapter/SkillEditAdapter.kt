package com.example.main_project.candidate.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.candidate.DataClasses.SkillShowDataClasses
import com.example.main_project.databinding.SkillEditBinding

class SkillEditAdapter : ListAdapter<SkillShowDataClasses, SkillEditAdapter.SkillViewHolder>(SkillDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val binding = SkillEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SkillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val skill = getItem(position)
        holder.bind(skill)
    }

    class SkillViewHolder(private val binding: SkillEditBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(skill: SkillShowDataClasses) {
            binding.skillEdit.setText(skill.skill)
            println("Binding skill item: $skill")
        }
    }

    private class SkillDiffCallback : DiffUtil.ItemCallback<SkillShowDataClasses>() {
        override fun areItemsTheSame(oldItem: SkillShowDataClasses, newItem: SkillShowDataClasses): Boolean {
            return oldItem.skill == newItem.skill
        }

        override fun areContentsTheSame(oldItem: SkillShowDataClasses, newItem: SkillShowDataClasses): Boolean {
            return oldItem == newItem
        }
    }
}

