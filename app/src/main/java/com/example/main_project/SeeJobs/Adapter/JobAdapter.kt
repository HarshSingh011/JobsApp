package com.example.main_project.SeeJobs.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.R
import com.example.main_project.SeeJobs.DataClasses.Job

class JobAdapter(
    private val jobList: List<Job>,
    private val onViewMoreClick: (Job) -> Unit
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_jobs_elements, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobList[position]
        holder.title.text = job.title
        holder.experience.text = "${job.experience} yr EXP"
        holder.salary.text = "₹${job.minSalary} - ₹${job.maxSalary}/Monthly"
        holder.location.text = job.location
        holder.requiredSkills.text = job.requiredSkills.joinToString(", ")
        holder.jobType.text = job.jobType
        holder.jobMode.text = job.mode

        holder.viewMoreButton.setOnClickListener {
            onViewMoreClick(job)
        }
    }

    override fun getItemCount(): Int = jobList.size

    inner class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.Title)
        val experience: TextView = itemView.findViewById(R.id.experience)
        val salary: TextView = itemView.findViewById(R.id.Salary)
        val location: TextView = itemView.findViewById(R.id.location)
        val requiredSkills: TextView = itemView.findViewById(R.id.requiredSkills)
        val jobType: TextView = itemView.findViewById(R.id.JobMode)
        val jobMode: TextView = itemView.findViewById(R.id.JobType)
        val viewMoreButton: View = itemView.findViewById(R.id.ViewMore)
    }
}

