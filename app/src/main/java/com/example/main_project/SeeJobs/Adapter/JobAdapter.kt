package com.example.main_project.SeeJobs.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.R
import com.example.main_project.SeeJobs.DataClasses.Job
import com.example.main_project.SeeJobs.DataClasses.SearchJobData
import com.example.main_project.databinding.SearchJobElementBinding

class JobAdapter(private val jobList: List<Job>) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_job_element, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobList[position]
        holder.title.text = job.title
        holder.experience.text = "${job.experience} yr EXP"
        holder.maxSalary.text = "$${job.maxSalary}/Month"
        holder.location.text = job.location
        holder.requiredSkills.text = job.requiredSkills.joinToString(", ")
    }

    override fun getItemCount(): Int {
        return jobList.size
    }

    inner class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.Title)
        val experience: TextView = itemView.findViewById(R.id.experience)
        val maxSalary: TextView = itemView.findViewById(R.id.MaximumSalary)
        val location: TextView = itemView.findViewById(R.id.location)
        val requiredSkills: TextView = itemView.findViewById(R.id.requiredSkills)
    }
}
