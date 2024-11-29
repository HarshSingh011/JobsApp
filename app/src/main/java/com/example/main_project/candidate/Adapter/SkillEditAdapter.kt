package com.example.main_project.candidate.Adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.candidate.DataClasses.SkillShowDataClasses
import com.example.main_project.databinding.SkillEditBinding

class SkillEditAdapter : ListAdapter<SkillShowDataClasses, SkillEditAdapter.SkillViewHolder>(SkillDiffCallback()) {

    private val itemList = mutableListOf<SkillShowDataClasses>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val binding = SkillEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SkillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val skill = getItem(position)
        holder.bind(skill)
    }

    override fun submitList(list: List<SkillShowDataClasses>?) {
        super.submitList(list)
        if (list != null) {
            itemList.clear()
            itemList.addAll(list)
        }
    }

    fun getUpdatedList(): List<SkillShowDataClasses> {
        return itemList
    }

    inner class SkillViewHolder(private val binding: SkillEditBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(skill: SkillShowDataClasses) {
            binding.skillEdit.setText(skill.skill)
            println("Binding skill item: $skill")

            binding.skillEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val updatedSkill = s.toString()
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        itemList[position] = SkillShowDataClasses(updatedSkill)
                    }
                }
            })
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

