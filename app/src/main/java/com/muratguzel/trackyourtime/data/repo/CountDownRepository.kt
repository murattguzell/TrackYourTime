package com.muratguzel.trackyourtime.data.repo

import android.content.Context

import com.muratguzel.trackyourtime.data.dataSource.CountdownDataSource
import com.muratguzel.trackyourtime.data.entitiy.CountDownTime
import android.icu.util.Calendar

class CountDownRepository (val cds: CountdownDataSource) {
    suspend fun  registerCountDown(title:String, notes:String, calendarDate: Calendar, calendarTime: Calendar,context: Context):Boolean = cds.registerCountDown(title,notes,calendarDate,calendarTime,context)

    suspend fun  getUserCountDown():List<CountDownTime> = cds.getUserCountDown()
    suspend fun  updateCountDown(title:String, notes:String, documentId:String, calendarDate: Calendar, calendarTime: Calendar,context: Context):Boolean = cds.updateCountDown(title,notes,documentId,calendarDate,calendarTime,context)
    suspend fun  deleteCountDownTimer(documentId:String,context: Context):Boolean = cds.deleteCountDownTimer(documentId,context)



}