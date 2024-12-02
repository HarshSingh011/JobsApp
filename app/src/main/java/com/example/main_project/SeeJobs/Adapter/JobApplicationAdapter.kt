package com.example.main_project.SeeJobs.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.main_project.SeeJobs.DataClasses.JobApplication
import com.example.main_project.databinding.AppliedBinding

class JobApplicationAdapter(
    private val jobApplications: MutableList<JobApplication>
) : RecyclerView.Adapter<JobApplicationAdapter.JobApplicationViewHolder>() {

    inner class JobApplicationViewHolder(private val binding: AppliedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jobApplication: JobApplication) {
            binding.Status.text = jobApplication.status
            binding.JobType.text = jobApplication.job.jobType ?: "N/A"
            binding.Skills.text = jobApplication.job.requiredSkills.joinToString(", ")

            Glide.with(binding.profileImage.context)
                .load(jobApplication.job.postedBy.profileImage)
                .thumbnail(
                    Glide.with(binding.profileImage.context)
                        .load(jobApplication.applicant.profileImageKey)
                        .placeholder(android.R.color.darker_gray)
                )
                .placeholder(android.R.color.darker_gray)
                .into(binding.profileImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobApplicationViewHolder {
        val binding = AppliedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return JobApplicationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobApplicationViewHolder, position: Int) {
        holder.bind(jobApplications[position])
    }

    override fun getItemCount(): Int = jobApplications.size

    fun addApplications(newApplications: List<JobApplication>) {
        jobApplications.addAll(newApplications)
        notifyDataSetChanged()
    }
}
