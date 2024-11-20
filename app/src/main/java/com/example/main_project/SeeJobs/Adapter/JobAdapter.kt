package com.example.main_project.SeeJobs.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.SeeJobs.DataClasses.SearchJobData
import com.example.main_project.databinding.SearchJobElementBinding

class JobAdapter(private val jobList: List<SearchJobData>) :
    RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    inner class JobViewHolder(private val binding: SearchJobElementBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(job: SearchJobData) {
            binding.heading.text = job.title
            binding.subheading.text = job.company
            binding.textelementone.text = job.type
            binding.textelementtwo.text = job.location
            binding.textelementthree.text = job.experience
            binding.Price.text = job.salary
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = SearchJobElementBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(jobList[position])
    }

    override fun getItemCount(): Int = jobList.size
}
