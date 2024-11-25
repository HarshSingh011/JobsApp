package com.example.main_project.candidate.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.candidate.DataClasses.ExperienceShowDataClasses
import com.example.main_project.databinding.ExperienceDetailsBinding

class ExperienceShowAdapter(private val experienceShowList: List<ExperienceShowDataClasses>) :
    RecyclerView.Adapter<ExperienceShowAdapter.ExperienceShowViewHolder>() {

    inner class ExperienceShowViewHolder(private val binding: ExperienceDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(experience: ExperienceShowDataClasses) {
            binding.CompanyName.text = experience.CompanyName
            binding.YearOfWork.text = experience.YearOfWork
            binding.Date.text = experience.Date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceShowViewHolder {
        val binding = ExperienceDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExperienceShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExperienceShowViewHolder, position: Int) {
        val experience = experienceShowList[position]
        holder.bind(experience)
    }

    override fun getItemCount(): Int = experienceShowList.size
}
