package com.example.main_project.SeeJobs.Fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.R
import com.example.main_project.SeeJobs.DataClasses.Job

class JobAdapter(
    private var jobList: MutableList<Job>, // Using MutableList to allow adding more jobs dynamically
    private val onViewMoreClick: (Job) -> Unit
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_job_element, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobList[position]
        holder.title.text = job.title
        holder.experience.text = "${job.experience} yr EXP"
        holder.salary.text = "₹${job.minSalary} - ₹${job.maxSalary}/Monthly"
        holder.location.text = job.location
        holder.requiredSkills.text = job.requiredSkills.joinToString(", ")
        holder.jobType.text = job.jobType ?: "Not Specified"
        holder.jobMode.text = job.mode ?: "Not Specified"

        holder.viewMoreButton.setOnClickListener {
            onViewMoreClick(job)
        }
    }

    override fun getItemCount(): Int = jobList.size

    // Method to add new jobs to the adapter
    fun addJobs(newJobs: List<Job>) {
        val startIndex = jobList.size
        jobList.addAll(newJobs)
        notifyItemRangeInserted(startIndex, newJobs.size)
    }

    inner class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.Title)
        val experience: TextView = itemView.findViewById(R.id.experience)
        val salary: TextView = itemView.findViewById(R.id.Salary)
        val location: TextView = itemView.findViewById(R.id.location)
        val requiredSkills: TextView = itemView.findViewById(R.id.requiredSkills)
        val jobType: TextView = itemView.findViewById(R.id.JobType)
        val jobMode: TextView = itemView.findViewById(R.id.JobMode)
        val viewMoreButton: View = itemView.findViewById(R.id.ViewMore)
    }
}
