package com.example.main_project.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.main_project.R
import com.example.main_project.Recruiter.DataClasses.JobContent
import com.example.main_project.databinding.SearchJobsElementsBinding

class RecruiterSeeJobsAdapter(private val jobList: List<JobContent>) : RecyclerView.Adapter<RecruiterSeeJobsAdapter.JobViewHolder>() {

    inner class JobViewHolder(val binding: SearchJobsElementsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = SearchJobsElementsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobList[position]
        holder.binding.apply {
            Glide.with(image.context)
                .load(job.postedBy.profileImage)
                .placeholder(R.drawable.bottomnav4)
                .error(R.drawable.bottomnav4)
                .into(image)
            Title.text = job.title
            location.text = job.location
            JobType.text = job.jobType
            experience.text = "${job.experience} years"
            JobMode.text = job.mode
            Salary.text = "$${job.minSalary} - $${job.maxSalary}"
            requiredSkills.text = job.requiredSkills?.joinToString(", ")
        }

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putLong("jobId", job.id)
                println("Job ID: ${job.id}")
            }

            it.findNavController().navigate(R.id.applicants, bundle)
        }
    }

    override fun getItemCount(): Int = jobList.size
}
