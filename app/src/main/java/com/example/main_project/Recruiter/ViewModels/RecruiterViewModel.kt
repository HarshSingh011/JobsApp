package com.example.main_project.SettingProfile.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.main_project.Recruiter.DataClasses.RecruiterData

class RecruiterViewModel : ViewModel() {

    private val _recruiterData = MutableLiveData<RecruiterData>()
    val recruiterData: LiveData<RecruiterData> get() = _recruiterData
    fun saveRecruiterData(recruiterData: RecruiterData) {
        _recruiterData.postValue(recruiterData)
    }
}
