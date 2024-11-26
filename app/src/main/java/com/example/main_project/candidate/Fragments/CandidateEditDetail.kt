package com.example.main_project.candidate.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.main_project.CandidateInterface
import com.example.main_project.CandidateProfileRetrofitClient
import com.example.main_project.candidate.Adapter.EducationEditAdapter
import com.example.main_project.candidate.Adapter.ExperienceEditAdapter
import com.example.main_project.candidate.Adapter.SkillEditAdapter
import com.example.main_project.candidate.DataClasses.*
import com.example.main_project.candidate.ViewModels.CandidateProfileViewModel
import com.example.main_project.databinding.FragmentCandidateEditDetailBinding
import kotlinx.coroutines.launch

class CandidateEditDetail : Fragment() {

    private var _binding: FragmentCandidateEditDetailBinding? = null
    private val binding get() = _binding!!

    private val candidateViewModel: CandidateProfileViewModel by activityViewModels()

    private lateinit var educationAdapter: EducationEditAdapter
    private lateinit var experienceAdapter: ExperienceEditAdapter
    private lateinit var skillAdapter: SkillEditAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCandidateEditDetailBinding.inflate(inflater, container, false)
        observeViewModel()

        binding.buttonUpdate.setOnClickListener {
            val data = collectDataFromRecyclerViews()
            updateCandidateData(data)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        educationAdapter = EducationEditAdapter()
        experienceAdapter = ExperienceEditAdapter()
        skillAdapter = SkillEditAdapter()

        binding.recyclerViewEducation.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = educationAdapter
        }

        binding.recyclerViewExperience.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = experienceAdapter
        }

        binding.recyclerViewSkill.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = skillAdapter
        }
    }

    private fun observeViewModel() {
        candidateViewModel.candidateData.observe(viewLifecycleOwner) { candidateData ->
            if (candidateData != null) {
                updateUI(candidateData)
            } else {
                Toast.makeText(context, "No candidate data available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(candidateData: CandidateDataGet) {
        candidateData.education.let { educationList ->
            val mappedEducationList = educationList.map { education ->
                EducationShowDataClasses(
                    IntituteName = education.institution?.trim() ?: "Unknown",
                    Degree = education.degree?.trim() ?: "Unknown",
                    DateOfCompletion = education.yearOfCompletion
                )
            }
            educationAdapter.submitList(mappedEducationList)
        }

        candidateData.experience.let { experienceList ->
            val mappedExperienceList = experienceList.map { experience ->
                ExperienceShowDataClasses(
                    CompanyName = experience.companyName?.trim() ?: "Unknown",
                    YearOfWork = experience.yearsWorked?.toString()?.trim() ?: "Unknown",
                    Date = experience.position?.trim() ?: "Unknown"
                )
            }
            experienceAdapter.submitList(mappedExperienceList)
        }

        candidateData.skill.let { skillList ->
            val mappedSkillList = skillList.map { skill ->
                SkillShowDataClasses(skill = skill.trim())
            }
            skillAdapter.submitList(mappedSkillList)
        }
    }

    private fun collectDataFromRecyclerViews(): Map<String, Any> {
        val educationList = (binding.recyclerViewEducation.adapter as EducationEditAdapter).currentList.map { education ->
            mapOf(
                "institution" to (education.IntituteName ?: ""),
                "degree" to (education.Degree ?: ""),
                "yearOfCompletion" to (education.DateOfCompletion ?: 0)
            )
        }

        val experienceList = (binding.recyclerViewExperience.adapter as ExperienceEditAdapter).currentList.map { experience ->
            mapOf(
                "companyName" to (experience.CompanyName ?: ""),
                "yearsWorked" to (experience.YearOfWork?.toIntOrNull() ?: 0),
                "position" to (experience.Date ?: "")
            )
        }

        val skillList = (binding.recyclerViewSkill.adapter as SkillEditAdapter).currentList.mapNotNull { it.skill }

        return mapOf(
            "education" to educationList,
            "experiences" to experienceList,
            "skill" to skillList
        )
    }

    private fun updateCandidateData(data: Map<String, Any>) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = CandidateProfileRetrofitClient.instance(requireContext())
                    .create(CandidateInterface::class.java)
                    .updateCandidate(data)

                if (response.isSuccessful) {
                    Toast.makeText(context, "Candidate updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to update candidate: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                println(e.message)
                Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

