package com.example.main_project.candidate.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.candidate.DataClasses.EducationShowDataClasses
import com.example.main_project.databinding.EducationDetailsBinding

class EducationShowAdapter(private val EducationShowList: List<EducationShowDataClasses>) :
    RecyclerView.Adapter<EducationShowAdapter.EducationShowViewHolder>() {

    inner class EducationShowViewHolder(private val binding: EducationDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(Education: EducationShowDataClasses) {
            binding.IntituteName.text = Education.IntituteName
            binding.Degree.text = Education.Degree
            binding.Date.text = Education.DateOfCompletion.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EducationShowViewHolder {
        val binding = EducationDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EducationShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EducationShowViewHolder, position: Int) {
        holder.bind(EducationShowList[position])
    }

    override fun getItemCount(): Int = EducationShowList.size
}
