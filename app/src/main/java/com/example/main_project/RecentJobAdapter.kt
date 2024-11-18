package com.example.main_project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.databinding.RecentlyPostedJobsElementBinding

class RecentJobAdapter(private val recentJobsList: List<RecentJobsData>) :
    RecyclerView.Adapter<RecentJobAdapter.RecentJobsViewHolder>() {

    inner class RecentJobsViewHolder(private val binding: RecentlyPostedJobsElementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(job: RecentJobsData) {
            binding.heading.text = job.title
            binding.subheading.text = job.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentJobsViewHolder {
        val binding = RecentlyPostedJobsElementBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecentJobsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentJobsViewHolder, position: Int) {
        holder.bind(recentJobsList[position])
    }

    override fun getItemCount(): Int = recentJobsList.size
}
