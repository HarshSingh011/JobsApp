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

        // Remove any existing TextWatchers to prevent duplicate listeners
        holder.textInputEditText.removeTextChangedListener(holder.textWatcher)

        // Create a new TextWatcher for this specific EditText
        holder.textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString().trim()
                filterValues[filterItem.id] = input
                Log.d("FilterAdapter", "Filter ${filterItem.name} updated: $input")
            }
        }

        // Add the new TextWatcher
        holder.textInputEditText.addTextChangedListener(holder.textWatcher)

        // Set the current value for this filter item
        holder.textInputEditText.setText(filterValues[filterItem.id] ?: "")
    }

    override fun getItemCount(): Int = filterItems.size

    fun getFilterValues(): Map<String, String> {
        val formattedFilters = mutableMapOf<String, String>()
        filterItems.forEach { filterItem ->
            val value = filterValues[filterItem.id]
            if (!value.isNullOrEmpty()) {
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
        var textWatcher: TextWatcher? = null

        fun bind(filterItem: FilterItem) {
            textInputEditText.hint = filterItem.name
        }
    }
}

