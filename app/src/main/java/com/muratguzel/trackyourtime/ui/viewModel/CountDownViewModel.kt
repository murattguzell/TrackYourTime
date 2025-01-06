package com.muratguzel.trackyourtime.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.muratguzel.trackyourtime.data.entitiy.CountDownTime
import com.muratguzel.trackyourtime.data.repo.CountDownRepository

import kotlinx.coroutines.launch
import android.icu.util.Calendar
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CountDownViewModel @Inject constructor(
    application: Application, val crepo: CountDownRepository,
) : AndroidViewModel(application) {


    var countDownList = MutableLiveData<List<CountDownTime>>()
    var updateCountDownStatus = MutableLiveData<Boolean>()
    var registerCountDownStatus = MutableLiveData<Boolean>()
    var deleteCountDownStatus = MutableLiveData<Boolean>()


    fun registerCountDown(
        title: String,
        notes: String,
        calendarDate: Calendar,
        calendarTime: Calendar,
    ) {
        viewModelScope.launch {
            registerCountDownStatus.value =
                crepo.registerCountDown(title, notes, calendarDate, calendarTime, getApplication())

        }
    }

    fun getUserCountDown() {
        viewModelScope.launch {
            countDownList.value = crepo.getUserCountDown()
        }
    }

    fun updateCountDown(
        title: String,
        notes: String,
        documentId: String,
        calendarDate: Calendar,
        calendarTime: Calendar,
    ) {
        viewModelScope.launch {
            updateCountDownStatus.value =
                crepo.updateCountDown(
                    title,
                    notes,
                    documentId,
                    calendarDate,
                    calendarTime,
                    getApplication()
                )
        }
    }

    fun deleteCountDownTimer(documentId: String) {
        viewModelScope.launch {
            deleteCountDownStatus.value = crepo.deleteCountDownTimer(documentId, getApplication())
        }

    }
}