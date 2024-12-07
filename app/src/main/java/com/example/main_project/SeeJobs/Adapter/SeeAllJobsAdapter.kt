package com.example.main_project.SeeJobs.Adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.main_project.R
import com.example.main_project.SeeJobs.DataClasses.Job
import com.example.main_project.databinding.SeealljobselementsBinding

class SeeAllJobsAdapter(private val onItemClick: (Job, Bundle) -> Unit) : ListAdapter<Job, SeeAllJobsAdapter.JobViewHolder>(JobDiffCallback()) {

    inner class JobViewHolder(private val binding: SeealljobselementsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(job: Job) {
            binding.apply {
                Glide.with(image.context)
                    .load(job.postedBy.profileImage)
                    .placeholder(R.drawable.bottomnav4)
                    .error(R.drawable.bottomnav4)
                    .into(image)
                Title.text = job.title
                location.text = job.location
                JobType.text = job.jobType ?: "N/A"
                JobMode.text = job.mode ?: "N/A"
                experience.text = "${job.experience}yr EXP"
                requiredSkills.text = job.requiredSkills.joinToString(", ")
                Salary.text = "$${job.minSalary} - $${job.maxSalary}/Month"
                Status.text = job.jobStatus
                Status.setBackgroundResource(
                    if (job.jobStatus == "CLOSED") R.drawable.statusrejectedelements
                    else R.drawable.statusacceptedelements
                )
                Status.setTextColor(
                    ContextCompat.getColor(
                        Status.context,
                        if (job.jobStatus == "CLOSED") android.R.color.white else android.R.color.black
                    )
                )
                root.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString("job_id", job.id.toString())
                        putString("job_title", job.title)
                        putString("job_description", job.description)
                        putString("job_salary", "${job.minSalary} - ${job.maxSalary}")
                        putString("job_location", job.location)
                        putString("job_mode", job.mode)
                        putString("job_type", job.jobType)
                        putString("job_profile_image", job.postedBy.profileImage)
                    }
                    onItemClick(job, bundle)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = SeealljobselementsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}

