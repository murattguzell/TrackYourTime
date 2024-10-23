package com.muratguzel.trackyourtime.ui.adapter

import CountdownHelper
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.data.entitiy.CountDownTime
import com.muratguzel.trackyourtime.databinding.CountdowntimerRowBinding
import java.util.Calendar

class CountDownTimeAdapter(val countDownTimeList: ArrayList<CountDownTime>,   private val onItemClicked: (CountDownTime) -> Unit) : RecyclerView.Adapter<CountDownTimeAdapter.CViewHolder>() {
    class CViewHolder(val binding:CountdowntimerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CViewHolder {
        val binding = CountdowntimerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return countDownTimeList.size
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: CViewHolder, position: Int) {
        val countDownTime = countDownTimeList[position]
        holder.binding.tvTargetTime.text = String.format(
            "%02d/%02d/%04d %02d:%02d",
            countDownTime.targetDay,
            countDownTime.targetMonth,
            countDownTime.targetYear,
            countDownTime.targetHour,
            countDownTime.targetMinute
        )
        if (countDownTime.title ==""){
            holder.binding.tvTitle.text = holder.itemView.context.getString(R.string.countdown)
        }else{
            holder.binding.tvTitle.text = countDownTime.title
        }
        Log.d("CountDownTime", "Binding position: $position, Time: $countDownTime")

        // Zamanlayıcıyı başlat
        val calendarDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, countDownTime.targetYear!!)
            set(Calendar.MONTH, countDownTime.targetMonth!!)
            set(Calendar.DAY_OF_MONTH, countDownTime.targetDay!!)
        }

        val calendarTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, countDownTime.targetHour!!)
            set(Calendar.MINUTE, countDownTime.targetMinute!!)
            set(Calendar.SECOND, countDownTime.targetSecond!!)
        }

        val countdownHelper = CountdownHelper(holder.binding, countDownTime.creationTime!!,holder.itemView.context)
        countdownHelper.startCountdown(calendarDate, calendarTime)
        holder.binding.cardView.setOnClickListener {
            onItemClicked(countDownTime)
        }

    }

}