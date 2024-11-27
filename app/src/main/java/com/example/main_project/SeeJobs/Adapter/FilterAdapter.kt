package com.example.main_project.SeeJobs.Adapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.textfield.TextInputEditText
import androidx.recyclerview.widget.RecyclerView
import com.example.main_project.R
import com.example.main_project.SeeJobs.DataClasses.FilterItem

class FilterAdapter(private val filterItems: List<FilterItem>) :
    RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    private val filterValues = mutableMapOf<Int, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.jobfilterelement, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filterItem = filterItems[position]
        holder.bind(filterItem)

        holder.textInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString().trim()
                if (input.isNotEmpty()) {
                    filterValues[filterItem.id] = input
                } else {
                    filterValues.remove(filterItem.id)
                }
                Log.d("FilterAdapter", "Current filters: $filterValues")
            }
        })
    }

    override fun getItemCount(): Int = filterItems.size

    fun getFilterValues(): Map<String, String> {
        val formattedFilters = mutableMapOf<String, String>()
        filterItems.forEach { filterItem ->
            val value = filterValues[filterItem.id]
            if (value != null) {
                when (filterItem.name.toLowerCase()) {
                    "title" -> formattedFilters["title"] = value
                    "location" -> formattedFilters["location"] = value
                    "min salary" -> formattedFilters["minSalary"] = value
                    "max salary" -> formattedFilters["maxSalary"] = value
                    "employment type" -> formattedFilters["employmentType"] = value
                    "experience" -> formattedFilters["experience"] = value
                    "skills" -> formattedFilters["requiredSkills"] = value
                    "mode" -> formattedFilters["mode"] = value.toUpperCase()
                    "job type" -> formattedFilters["jobType"] = value
                }
            }
        }
        Log.d("FilterAdapter", "Formatted filters: $formattedFilters")
        return formattedFilters
    }

    class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textInputEditText: TextInputEditText = itemView.findViewById(R.id.filterInputName)

        fun bind(filterItem: FilterItem) {
            textInputEditText.hint = filterItem.name
        }
    }
}

