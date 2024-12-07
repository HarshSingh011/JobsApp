package com.example.main_project.SeeJobs.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.R
import com.example.main_project.databinding.AlljobselemetsBinding

class SeeAllJobsFilterAdapter(private val onFilterSelected: (String) -> Unit) :
    RecyclerView.Adapter<SeeAllJobsFilterAdapter.FilterViewHolder>() {

    private val filters = listOf("open", "closed", "paused")
    private var selectedPosition = 0

    inner class FilterViewHolder(private val binding: AlljobselemetsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(filter: String, isSelected: Boolean) {
            binding.buttonfilter.apply {
                text = filter.capitalize()
                if (isSelected) {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.background))
                    setTextColor(ContextCompat.getColor(context, android.R.color.white))
                } else {
                    setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                    setTextColor(ContextCompat.getColor(context, android.R.color.black))
                }
                setOnClickListener {
                    val oldPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(oldPosition)
                    notifyItemChanged(selectedPosition)
                    onFilterSelected(filter)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val binding = AlljobselemetsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(filters[position], position == selectedPosition)
    }

    override fun getItemCount() = filters.size
}

