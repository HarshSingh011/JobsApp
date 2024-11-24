package com.example.main_project.candidate.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.candidate.DataClasses.SkillShowDataClasses
import com.example.main_project.databinding.SkillDetailsBinding

class SkillShowAdapter(private val skillList: List<SkillShowDataClasses>) :
    RecyclerView.Adapter<SkillShowAdapter.SkillViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val binding = SkillDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SkillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val skill = skillList[position]
        holder.bind(skill)
    }

    override fun getItemCount(): Int = skillList.size

    class SkillViewHolder(private val binding: SkillDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(skill: SkillShowDataClasses) {
            binding.Work.text = skill.skill
        }
    }
}
