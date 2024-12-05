package com.example.main_project.SeeJobs.Fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.main_project.R
import com.example.main_project.SeeJobs.DataClasses.Job

class SearchJobAdapter(
    private var jobList: MutableList<Job>,
    private val onViewMoreClick: (Job) -> Unit
) : RecyclerView.Adapter<SearchJobAdapter.JobViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.searchboxelement, parent, false)
        return JobViewHolder(view)
    }
    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobList[position]
        Glide.with(holder.image.context)
            .load(job.postedBy.profileImage)
            .placeholder(R.drawable.bottomnav4)
            .error(R.drawable.bottomnav4)
            .into(holder.image)
        holder.title.text = job.title
        holder.experience.text = "${job.experience}yr EXP"
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
    fun updateJobs(newJobs: List<Job>) {
        jobList.clear()
        jobList.addAll(newJobs)
        notifyDataSetChanged()
    }
    fun getSearchJobElementsCount(): Int {
        return itemCount
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
        val image: ImageView = itemView.findViewById(R.id.image)
    }
}