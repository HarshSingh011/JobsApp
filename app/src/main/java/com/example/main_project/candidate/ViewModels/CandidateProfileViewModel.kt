package com.example.main_project.candidate.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.main_project.candidate.DataClasses.CandidateDataGet

class CandidateProfileViewModel : ViewModel() {
    private val _candidateData = MutableLiveData<CandidateDataGet>()
    val candidateData: LiveData<CandidateDataGet> = _candidateData

    fun setCandidateData(candidateData: CandidateDataGet) {
        println("Setting candidate data in ViewModel: $candidateData")
        _candidateData.value = candidateData
    }

    fun getCandidateData(): CandidateDataGet? {
        return _candidateData.value
    }
}